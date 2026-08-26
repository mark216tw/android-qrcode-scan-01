# 更新紀錄

## [1.0.0-debug.2] - 2026-08-26

第二個 Debug 測試版本，新增常見一維條碼掃描功能。

### 新增

- 相機與圖片的一維條碼辨識
- EAN-8、EAN-13、UPC-A、UPC-E、Code 39、Code 93、Code 128、ITF 及 Codabar 格式

### 調整

- 掃描框改為兼容 QR Code 與橫向一維條碼的較寬版面
- 更新掃描提示、隱私說明及複製內容標籤
- 商品碼維持文字格式，保留前導零

### 驗證

- 3 個內容分類單元測試通過
- Android Lint 通過，0 errors
- Debug APK 建置成功
- 合併後的 Manifest 未包含網路權限

## [1.0.0-debug.1] - 2026-08-23

第一個 Debug 測試版本，非正式發行版本。

### 新增

- 相機即時 QR Code 掃描
- 圖片 QR Code 辨識
- 手電筒、提示音與震動回饋
- 多種 QR Code 內容分類及系統操作
- 本機掃描紀錄管理
- 六種可即時切換的主題色彩
- 明亮與深色模式即時切換
- 系統狀態列及底部導覽列明暗同步
- 雙層 Adaptive App Icon

### 驗證

- Debug APK 建置成功
- QR Code 內容分類單元測試通過
- Android Lint 通過
