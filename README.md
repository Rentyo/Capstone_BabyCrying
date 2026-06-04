# Hear4You - Baby Crying Detection App

청각장애 보호자를 위한 **아이 울음소리 감지 및 원인 분류 알림 서비스**입니다.
Raspberry Pi와 USB 마이크를 통해 아이 울음소리를 수집하고, AI 모델이 울음 원인을 분류한 뒤 Android 앱으로 보호자에게 알림을 전달하는 것을 목표로 개발했습니다.

<br>

## 📌 프로젝트 개요

청각장애 보호자는 아이의 울음소리를 즉각적으로 인지하기 어렵기 때문에, 아이가 울고 있는 상황을 놓칠 수 있습니다.
Hear4You는 이러한 문제를 해결하기 위해 아이 울음소리를 감지하고, 단순히 “울음 발생”을 알려주는 것에서 나아가 울음 원인을 분류하여 보호자가 상황에 맞게 대응할 수 있도록 돕는 서비스입니다.

본 저장소는 Hear4You 프로젝트 중 **Android 앱 영역**을 중심으로 구성되어 있습니다.

<br>

## 🎯 프로젝트 목표

* 아이 울음소리 감지 시 Android 앱으로 실시간 알림 전달
* Bluetooth 통신을 통해 Raspberry Pi와 Android 앱 연동
* 울음 원인을 배고픔, 복통, 불편함, 졸림 등으로 분류
* 감지 이력을 로컬 DB에 저장하여 통계 및 기록 확인
* 앱이 백그라운드에 있어도 알림이 유지되도록 Foreground Service 적용

<br>

## 🛠 기술 스택

### Android

* Java
* Kotlin 일부
* Android SDK
* AppCompat
* ViewBinding
* ViewPager2
* Material Components

### Local Database

* Room
* SQLite

### Communication

* Bluetooth RFCOMM Socket
* Raspberry Pi ↔ Android Bluetooth 통신

### Visualization

* MPAndroidChart

### AI / Embedded

* Raspberry Pi
* USB Microphone
* LM393 Sound Sensor
* Python
* VGG16
* AST(Audio Spectrogram Transformer)
* Conv1D
* Dense Layer

<br>

## 🧩 주요 기능

### 1. Bluetooth 기기 탐색 및 연결

Android 앱에서 주변 Bluetooth 기기를 탐색하고, 사용자가 선택한 Raspberry Pi와 연결합니다.
연결된 기기에서 울음 감지 결과를 수신하면 앱 내부 Handler를 통해 메시지를 처리합니다.

주요 처리 흐름은 다음과 같습니다.

1. Bluetooth 활성화 확인
2. 주변 기기 탐색
3. 기기 목록 표시
4. 선택한 기기와 RFCOMM Socket 연결
5. Raspberry Pi에서 전송한 감지 결과 수신

<br>

### 2. 울음소리 감지 알림

Raspberry Pi에서 전송된 메시지에 따라 Android 알림을 생성합니다.

분류 항목은 다음과 같습니다.

| 수신 메시지        | 앱 표시 내용  |
| ------------- | -------- |
| `bellypain`   | 복통       |
| `discomfort`  | 불편함      |
| `hungry`      | 배고픔      |
| `tired`       | 피곤함 / 졸림 |
| `아이 울음 소리 감지` | 울음 감지    |

앱은 감지 결과를 Notification으로 표시하고, 감지 시각과 함께 사용자에게 전달합니다.

<br>

### 3. Foreground Service 기반 연결 유지

울음소리 감지는 보호자가 앱 화면을 계속 보고 있지 않아도 동작해야 합니다.
이를 위해 Bluetooth 연결을 Foreground Service에서 유지하도록 구현했습니다.

Foreground Service를 사용하여 다음을 처리했습니다.

* 앱 실행 중 Bluetooth 연결 유지
* 서비스 실행 상태를 알림으로 표시
* 서비스 종료 버튼 제공
* 백그라운드 상황에서도 감지 메시지 수신 가능하도록 구성

<br>

### 4. Room 기반 감지 이력 저장

감지된 울음소리 정보는 Room DB에 저장됩니다.

저장 데이터는 다음과 같습니다.

| 필드        | 설명       |
| --------- | -------- |
| `id`      | 로그 고유 ID |
| `CryType` | 울음 유형    |
| `date`    | 감지 날짜    |
| `time`    | 감지 시간    |
| `userId`  | 사용자 ID   |

이를 통해 사용자는 감지 이력을 확인하고, 날짜별 울음 발생 기록을 조회할 수 있습니다.

<br>

### 5. 통계 및 기록 확인

Room DB에 저장된 감지 이력을 기반으로 날짜별 기록과 울음 유형을 확인할 수 있도록 구성했습니다.
MPAndroidChart를 활용해 감지 데이터를 시각화할 수 있는 구조를 포함했습니다.

<br>

## 🧠 AI 모델 개선

초기에는 VGG16 기반 모델을 활용했지만, 짧은 음성 구간과 아이 울음소리의 고유한 특성을 충분히 반영하지 못해 정확도에 한계가 있었습니다.

이를 개선하기 위해 다음과 같은 작업을 진행했습니다.

* 오디오 샘플링레이트 및 입력 길이 점검
* 음성 스펙트로그램 기반 특징 추출 구조 개선
* AST(Audio Spectrogram Transformer) 기반 모델 적용
* Conv1D와 Dense 계층을 결합한 분류 구조 개선
* 입력 데이터 형태 불일치 문제 해결
* 전처리 파이프라인 정비

그 결과 울음소리 분류 정확도를 **45.3%에서 62.8%까지 개선**했습니다.

<br>

## 🏗 시스템 구조

```text
[USB Microphone / LM393 Sensor]
              ↓
        [Raspberry Pi]
              ↓
  울음소리 감지 및 AI 분류
              ↓
      Bluetooth Message
              ↓
        [Android App]
              ↓
  알림 표시 / 감지 로그 저장 / 통계 확인
```

<br>

## 📱 Android 앱 구조

```text
app
 └── src
     └── main
         ├── java/com/example/a1215dday
         │   ├── BluetoothActivity.java
         │   ├── BluetoothManager.java
         │   ├── BluetoothService.java
         │   ├── FragmentActivity.java
         │   ├── AccountFragment.java
         │   ├── StatisticsFragment.java
         │   └── room
         │       ├── BabyCryLogoDB.java
         │       ├── BabyCryLogoDao.java
         │       └── BabyCryLogoData.java
         └── res
```

<br>

## 🔄 주요 동작 흐름

### Bluetooth 연결

```text
앱 실행
  ↓
Bluetooth 권한 확인
  ↓
주변 기기 탐색
  ↓
Raspberry Pi 선택
  ↓
BluetoothService 실행
  ↓
RFCOMM Socket 연결
  ↓
감지 메시지 수신 대기
```

### 울음 감지 처리

```text
Raspberry Pi에서 메시지 전송
  ↓
Android BluetoothManager에서 메시지 수신
  ↓
울음 유형 분기 처리
  ↓
Notification 표시
  ↓
Room DB에 감지 로그 저장
```

<br>

## 📊 프로젝트 성과

* 아이 울음소리 분류 정확도 **45.3% → 62.8% 개선**
* Raspberry Pi와 Android 앱 간 Bluetooth 통신 구현
* Foreground Service를 활용한 알림 안정성 확보
* Room DB 기반 울음 감지 로그 저장 구조 구현
* AI·빅데이터 분야 캡스톤디자인 최우수 성과 인정

<br>

## 🧑‍💻 담당 역할

* Android 앱 개발
* Bluetooth 기기 탐색 및 연결 기능 구현
* Raspberry Pi와 Android 간 Bluetooth 통신 구조 구현
* Foreground Service 기반 연결 유지 및 알림 처리
* Room DB 기반 울음 감지 로그 저장 구조 설계
* 울음소리 분류 모델 개선 및 전처리 과정 점검
* 실제 사용 환경을 고려한 알림 흐름 설계

<br>

## 🍓 Raspberry Pi와의 연동

본 Android 앱 프로젝트는 Raspberry Pi 저장소와 함께 동작합니다.

Raspberry Pi는 LM393 사운드 센서와 USB 마이크를 활용해 아이 울음소리를 감지하고, AI 모델을 통해 울음 원인을 분류합니다. 이후 분류 결과를 Bluetooth 메시지로 Android 앱에 전송합니다.

Android 앱은 Raspberry Pi에서 전송한 Bluetooth 메시지를 수신하고, 수신된 값에 따라 보호자에게 울음 감지 및 원인 분류 알림을 표시합니다.

Raspberry Pi 저장소는 아래 링크에서 확인할 수 있습니다.

👉 [RaspberryPiBabyCrying](https://github.com/Rentyo/RaspberryPiBabyCrying)


## 🚀 실행 방법

### 1. Repository Clone

```bash
git clone https://github.com/Rentyo/Capstone_BabyCrying.git
cd Capstone_BabyCrying
```

### 2. Android Studio에서 프로젝트 열기

Android Studio에서 해당 프로젝트 폴더를 열고 Gradle Sync를 진행합니다.

### 3. 실행 환경

* Android Studio
* Android SDK 34
* minSdk 26 이상
* Bluetooth 사용 가능 Android 기기
* Raspberry Pi Bluetooth 연결 환경

### 4. 앱 실행

Android 기기에서 앱을 실행한 뒤 Bluetooth 권한을 허용하고, Raspberry Pi 기기를 선택하여 연결합니다.

<br>

## ⚠️ 참고 사항

* 본 저장소는 Android 앱 중심 코드로 구성되어 있습니다.
* AI 모델 학습 코드와 Raspberry Pi 측 음성 수집 및 추론 코드는 별도 환경에서 구성되었습니다.
* Bluetooth 연결을 위해 Raspberry Pi와 Android 기기가 사전에 페어링되어 있어야 합니다.
* Android 버전에 따라 Bluetooth 권한 요청 방식이 다를 수 있습니다.

<br>

## 📌 개선 방향

* AI 모델 경량화 및 모바일/엣지 추론 최적화
* 감지 정확도 향상을 위한 데이터셋 확장
* Raspberry Pi 측 코드 저장소 분리 및 문서화
* 사용자별 감지 기록 관리 기능 고도화
* 알림 유형 및 통계 화면 UI 개선
* Room DB 비동기 처리 구조 개선

<br>

## 🏷 Keywords

`Android` `Java` `Bluetooth` `Raspberry Pi` `Room` `Foreground Service` `Baby Cry Detection` `AI` `Audio Classification`
