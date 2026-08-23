# 簡單QR掃描

一款以快速、簡單與隱私為核心的 Android QR Code 掃描工具。支援相機即時掃描及圖片辨識，掃描內容只在裝置端處理，不需要網路連線。

> 目前提供的是 `v1.0.0-debug.1` Debug 測試版本，並非正式發行版本。

## 主要功能

- 使用相機即時辨識 QR Code
- 支援從圖片選擇 QR Code
- 支援手電筒、自動對焦、掃描提示音與震動
- 辨識網址、Wi-Fi、電話、Email、簡訊、位置、聯絡人、行事曆及一般文字
- 提供複製、分享與對應的 Android 系統操作
- 掃描紀錄僅保存在本機，可單筆刪除或全部清除
- 可選擇不保存掃描紀錄
- 六種主題色彩：葡萄紫、海洋藍、薄荷綠、珊瑚紅、陽光橘、莓果粉
- 支援明亮模式與深色模式即時切換
- Android 系統狀態列及底部導覽列會同步目前的明暗模式

## 隱私設計

- QR Code 使用裝置端模型辨識，不上傳掃描內容
- App 不要求網路權限
- 僅在使用相機掃描時要求相機權限
- 圖片透過 Android Photo Picker 選擇，不讀取整個相簿
- 掃描紀錄與偏好設定保存在本機
- 已停用 Android 雲端備份
- 掃描網址後不會自動開啟，使用者需確認後操作

## 系統需求

- Android 8.0（API 26）或以上
- 具備相機的 Android 裝置
- JDK 17
- Android SDK 36

## 技術架構

- Kotlin
- Jetpack Compose + Material 3
- CameraX
- Google ML Kit Barcode Scanning
- SharedPreferences 本機資料保存
- Gradle 8.11.1

## 本機建置

Windows PowerShell：

```powershell
.\gradlew.bat testDebugUnitTest lintDebug assembleDebug
```

建置完成後，Debug APK 位於：

```text
app/build/outputs/apk/debug/app-debug.apk
```

Debug APK 使用 Android Debug 簽章，只適合功能測試，不應作為正式上架版本使用。

## App 圖示

- Adaptive Icon 最終尺寸：`108 x 108 dp`
- 外框裁切區：`72 x 72 dp`
- 核心安全區：`66 x 66 dp`
- 核心圖案尺寸：約 `59.4 x 59.4 dp`，佔安全區 90%
- 使用獨立前景與背景圖層
- 視覺風格為活潑、友善、年輕化的粗線條卡通工具圖示

## 版本狀態

目前版本為 `v1.0.0-debug.1` Pre-release。版本內容請參考 [CHANGELOG.md](CHANGELOG.md)。
