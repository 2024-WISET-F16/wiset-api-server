# wiset-api-server

<br>

## Git 브랜치 워크플로우 가이드(feat. ChatGPT)

<br>

## 브랜치 구조
- **main**: 배포 가능한 코드가 있는 브랜치입니다.
- **develop**: 개발 중인 기능이 통합되는 브랜치입니다.
- **feature**: 특정 기능 개발을 위한 브랜치입니다.(ex. feature/login) 기능 개발이 완료되면 `develop` 브랜치로 병합됩니다.

<br>

## 작업 절차

### 1. 저장소 클론 및 초기 설정

1. 리포지토리를 클론합니다.
    ```bash
    git clone https://github.com/your-username/your-repository.git
    ```

2. `develop` 브랜치로 전환합니다.
    ```bash
    cd your-repository
    git checkout develop
    ```
<br>

### 2. 새로운 feature 브랜치 생성

1. `develop` 브랜치에서 새로운 `feature` 브랜치를 생성합니다.
    ```bash
    git checkout -b feature/your-feature-name
    ```
<br>

### 3. 기능 개발

1. 기능 개발 작업을 수행합니다.
2. 변경 사항을 스테이징하고 커밋합니다.
    ```bash
    git add .
    git commit -m "Add your feature description"
    ```
<br>

### 4. feature 브랜치를 develop 브랜치에 병합

1. `develop` 브랜치로 전환합니다.
    ```bash
    git checkout develop
    ```

2. 로컬 `develop` 브랜치를 최신 상태로 업데이트합니다.
    ```bash
    git pull origin develop
    ```

3. `feature` 브랜치를 `develop` 브랜치에 병합합니다.
    ```bash
    git merge feature/your-feature-name
    ```

4. 병합 충돌이 발생할 경우, 충돌을 해결하고 병합을 완료합니다. 충돌 해결 후 변경 사항을 커밋합니다.
    ```bash
    git add .
    git commit -m "Resolve merge conflicts"
    ```
<br>

### 5. develop 브랜치로 푸시

1. 로컬 `develop` 브랜치를 원격 저장소에 푸시합니다.
    ```bash
    git push origin develop
    ```
