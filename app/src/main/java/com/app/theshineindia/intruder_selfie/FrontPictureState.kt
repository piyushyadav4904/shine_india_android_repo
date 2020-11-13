package com.app.theshineindia.intruder_selfie

sealed class FrontPictureState {
    class Taken(val filePath: String) : FrontPictureState()
    class Error(val error: Throwable) : FrontPictureState()
    class Started() : FrontPictureState()
    class Destroyed() : FrontPictureState()
}