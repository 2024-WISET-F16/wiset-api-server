package com.example.wisetapiserver.service.sunposition;

import java.time.LocalDateTime;

public class SunPosition {
    private static final double LATITUDE = 36.851221; // 공주대 천안캠의 위도
    private static final double LONGITUDE = 127.152924; // 공주대 천안캠의 경도

    public static double[] calculateAzEl(LocalDateTime dateTime) {
        int year = dateTime.getYear();
        int month = dateTime.getMonthValue();
        int day = dateTime.getDayOfMonth();
        int hour = dateTime.getHour();
        int minute = dateTime.getMinute();
        int second = dateTime.getSecond();
        int utcOffset = 9; // 한국 시간대 고정 (UTC+09:00)

        double mins = hour * 60 + minute + second / 60.0;
        double jday = getJD(year, month, day);
        double total = jday + mins / 1440.0 - utcOffset / 24.0;
        double T = getTimeJulianCent(total);
        return getAzEl(T, mins, LATITUDE, LONGITUDE, utcOffset);
    }

    private static double degToRad(double angleDeg) {
        return angleDeg * Math.PI / 180.0;
    }

    private static double radToDeg(double angleRad) {
        return angleRad * 180.0 / Math.PI;
    }

    private static double getTimeJulianCent(double jday) {
        return (jday - 2451545.0) / 36525.0;
    }

    private static double getJD(int year, int month, int day) {
        if (month <= 2) {
            year -= 1;
            month += 12;
        }
        double A = Math.floor(year / 100.0);
        double B = 2 - A + Math.floor(A / 4);
        double JD = Math.floor(365.25 * (year + 4716)) + Math.floor(30.6001 * (month + 1)) + day + B - 1524.5;
        return JD;
    }

    private static double[] getAzEl(double T, double mins, double lat, double lng, int utcOffset) {
        double eqTime = getEquationOfTime(T);
        double theta = getSunDeclination(T);

        double solarTimeFix = eqTime + 4.0 * lng - 60.0 * utcOffset;
        double trueSolarTime = mins + solarTimeFix;
        while (trueSolarTime > 1440)
            trueSolarTime -= 1440;
        double hourAngle = trueSolarTime / 4.0 - 180.0;
        if (hourAngle < -180)
            hourAngle += 360.0;
        double haRad = degToRad(hourAngle);
        double csz = Math.sin(degToRad(lat)) * Math.sin(degToRad(theta)) + Math.cos(degToRad(lat)) * Math.cos(degToRad(theta)) * Math.cos(haRad);
        if (csz > 1.0)
            csz = 1.0;
        else if (csz < -1.0)
            csz = -1.0;
        double zenith = radToDeg(Math.acos(csz));
        double azDenom = Math.cos(degToRad(lat)) * Math.sin(degToRad(zenith));
        double azimuth;
        if (Math.abs(azDenom) > 0.001) {
            double azRad = ((Math.sin(degToRad(lat)) * Math.cos(degToRad(zenith))) - Math.sin(degToRad(theta))) / azDenom;
            if (Math.abs(azRad) > 1.0) {
                if (azRad < 0)
                    azRad = -1.0;
                else
                    azRad = 1.0;
            }
            azimuth = 180.0 - radToDeg(Math.acos(azRad));
            if (hourAngle > 0.0)
                azimuth = -azimuth;
        } else {
            if (lat > 0.0)
                azimuth = 180.0;
            else
                azimuth = 0.0;
        }
        if (azimuth < 0.0)
            azimuth += 360.0;
        double exoatmElevation = 90.0 - zenith;

        // Atmospheric Refraction correction
        double refractionCorrection = getRefraction(exoatmElevation);

        double solarZen = zenith - refractionCorrection;
        double elevation = 90.0 - solarZen;
        return new double[]{azimuth, elevation};
    }

    private static double getEquationOfTime(double T) {
        double epsilon = getObliquityCorrection(T);
        double l0 = getGeomMeanLongSun(T);
        double e = getEccentricityEarthOrbit(T);
        double m = getGeomMeanAnomalySun(T);

        double y = Math.tan(degToRad(epsilon) / 2.0);
        y *= y;

        double sin2l0 = Math.sin(2.0 * degToRad(l0));
        double sinm = Math.sin(degToRad(m));
        double cos2l0 = Math.cos(2.0 * degToRad(l0));
        double sin4l0 = Math.sin(4.0 * degToRad(l0));
        double sin2m = Math.sin(2.0 * degToRad(m));

        double Etime = y * sin2l0 - 2.0 * e * sinm + 4.0 * e * y * sinm * cos2l0 - 0.5 * y * y * sin4l0 - 1.25 * e * e * sin2m;
        return radToDeg(Etime) * 4.0;	// in minutes of time
    }

    private static double getSunDeclination(double T) {
        double e = getObliquityCorrection(T);
        double lambda = getSunApparentLong(T);
        double sint = Math.sin(degToRad(e)) * Math.sin(degToRad(lambda));
        double theta = radToDeg(Math.asin(sint));
        return theta;   // in degrees
    }

    private static double getObliquityCorrection(double T) {
        double e0 = getMeanObliquityOfEcliptic(T);
        double omega = 125.04 - 1934.136 * T;
        return e0 + 0.00256 * Math.cos(degToRad(omega));
    }

    private static double getSunApparentLong(double T) {
        double o = getSunTrueLong(T);
        double omega = 125.04 - 1934.136 * T;
        double lambda = o - 0.00569 - 0.00478 * Math.sin(degToRad(omega));
        return lambda;  // in degrees
    }

    private static double getSunTrueLong(double T) {
        double l0 = getGeomMeanLongSun(T);
        double c = getSunEqOfCenter(T);
        double O = l0 + c;
        return O;		// in degrees
    }

    private static double getSunEqOfCenter(double T) {
        double m = getGeomMeanAnomalySun(T);
        double mrad = degToRad(m);
        double sinm = Math.sin(mrad);
        double sin2m = Math.sin(mrad + mrad);
        double sin3m = Math.sin(mrad + mrad + mrad);
        double C = sinm * (1.914602 - T * (0.004817 + 0.000014 * T)) + sin2m * (0.019993 - 0.000101 * T) + sin3m * 0.000289;
        return C;   // in degrees
    }

    private static double getMeanObliquityOfEcliptic(double T) {
        double seconds = 21.448 - T * (46.8150 + T * (0.00059 - T * (0.001813)));
        return 23.0 + (26.0 + (seconds / 60.0)) / 60.0;
    }

    private static double getGeomMeanLongSun(double T) {
        double L0 = 280.46646 + T * (36000.76983 + T * (0.0003032));
        while (L0 > 360.0)
            L0 -= 360.0;
        while (L0 < 0.0)
            L0 += 360.0;
        return L0;
    }

    private static double getEccentricityEarthOrbit(double T) {
        double e = 0.016708634 - T * (0.000042037 + 0.0000001267 * T);
        return e;   // unitless
    }

    private static double getGeomMeanAnomalySun(double T) {
        double M = 357.52911 + T * (35999.05029 - 0.0001537 * T);
        return M;   // in degrees
    }

    private static double getRefraction(double elev) {
        double correction;
        if (elev > 85.0) {
            correction = 0.0;
        } else {
            double te = Math.tan(degToRad(elev));
            if (elev > 5.0)
                correction = 58.1 / te - 0.07 / (te * te * te) + 0.000086 / (te * te * te * te * te);
            else if (elev > -0.575)
                correction = 1735.0 + elev * (-518.2 + elev * (103.4 + elev * (-12.79 + elev * 0.711)));
            else
                correction = -20.774 / te;
            correction = correction / 3600.0;
        }
        return correction;
    }
}
