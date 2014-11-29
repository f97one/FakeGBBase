FakeGPBase
==========

NFCタグに反応するGPベースっぽいもの

## 開発環境
* Android Studio 1.0 RC2
* Android SDK 23.0.5

## ビルドについて
以下の環境変数を定義してください。
Windowsならユーザー環境変数で、Linuxなら ` .profile` でOKです。

| 変数 | 内容 |
|:--------------------------|:-------------------------------------------|
| DEBUG_KEYSTORE            | デバッグ証明書へのフルパス                 |
| RELEASE_KEYSTORE          | リリース証明書ストアへのフルパス           |
| RELEASE_KEYSTORE_PASSWORD | リリース証明書ストアのキーストアパスワード |
| RELEASE_KEY_ALIAS         | リリース証明書ストアの証明書エイリアス名称 |
| RELEASE_KEY_PASSWORD      | リリース証明書エイリアスのパスワード       |

