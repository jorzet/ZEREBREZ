/*
 * Copyright [2019] [Jorge Zepeda Tinoco]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.zerebrez.zerebrez.models

/**
 * Created by Jorge Zepeda Tinoco on 28/05/18.
 * jorzet.94@gmail.com
 */

class Image {
    private var mImageId : Integer = Integer(0)
    private var mIsDownloadable : Boolean = false
    private var mNameInStorage : String = ""

    fun setImageId(imageId : Integer) {
        this.mImageId = imageId
    }

    fun getImageId() : Integer {
        return this.mImageId
    }

    fun setIsDownloadable(isDownloadable : Boolean) {
        this.mIsDownloadable = isDownloadable
    }

    fun isDownloadable() : Boolean {
        return this.mIsDownloadable
    }

    fun setNameInStorage(nameInStorage : String) {
        this.mNameInStorage = nameInStorage
    }

    fun getNameInStorage() : String {
        return this.mNameInStorage
    }

}