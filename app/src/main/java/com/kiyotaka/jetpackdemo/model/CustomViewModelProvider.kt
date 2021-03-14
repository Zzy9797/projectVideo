package com.kiyotaka.jetpackdemo.model

import android.content.Context
import com.kiyotaka.jetpackdemo.db.RepositoryProvider
import com.kiyotaka.jetpackdemo.db.repository.RecordRepository
import com.kiyotaka.jetpackdemo.db.repository.UserRepository
import com.kiyotaka.jetpackdemo.model.factory.*

object CustomViewModelProvider {

    fun providerRegisterModel(context: Context): RegisterModelFactory {
        val repository: UserRepository = RepositoryProvider.providerUserRepository(context)
        return RegisterModelFactory(repository)
    }

    fun providerLoginModel(context: Context): LoginModelFactory {
        val repository: UserRepository = RepositoryProvider.providerUserRepository(context)
        return LoginModelFactory(repository)
    }

    fun providerFindBackModel(): FindBackModelFactory {
        return FindBackModelFactory()
    }

    fun providerHomeModel(context: Context): HomeModelFactory {
        val repository: RecordRepository = RepositoryProvider.providerRecordRepository(context)
        return HomeModelFactory(repository)
    }

    fun providerCenterModel(context: Context): CenterModelFactory {
        val repository: UserRepository = RepositoryProvider.providerUserRepository(context)
        return CenterModelFactory(repository)
    }

    fun providerDataModel(context: Context): DataModelFactory {
        val repository: UserRepository = RepositoryProvider.providerUserRepository(context)
        return DataModelFactory(repository)
    }

    fun providerChangeKeyModel(): ChangeKeyModelFactory {
        return ChangeKeyModelFactory()
    }

    fun providerVerifyModel(): VerifyModelFactory {
        return VerifyModelFactory()
    }

    fun providerRecordModel(context: Context): RecordModelFactory {
        val repository: RecordRepository = RepositoryProvider.providerRecordRepository(context)
        return RecordModelFactory(repository)
    }

    fun providerDetailModel(context: Context): DetailModelFactory {
        val repository: RecordRepository = RepositoryProvider.providerRecordRepository(context)
        return DetailModelFactory(repository)
    }

    fun providerCountModel(): CountModelFactory {
        return CountModelFactory()
    }

    fun providerMediaModel(): MediaModelFactory {
        return MediaModelFactory()
    }

    fun providerSecretModel(): ScretModelFactory {
        return ScretModelFactory()
    }

    fun providerCheckRecordModel(): CheckRecordModelFactory {
        return CheckRecordModelFactory()
    }

    fun providerAboutModel(): AboutModelFactory {
        return AboutModelFactory()
    }

    fun providerSettingModel(): SettingModelFactory {
        return SettingModelFactory()
    }

    fun providerSuggestionModel(): SuggestionModelFactory{
        return SuggestionModelFactory()
    }
}