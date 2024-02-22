package com.mlkang.codingexercise

import Weatherpb.WeatherPb
import android.content.Context
import androidx.datastore.core.DataStore
import com.mlkang.codingexercise.data.GeocodingRemoteDataSource
import com.mlkang.codingexercise.data.WeatherRemoteDataSource
import com.mlkang.codingexercise.data.weatherStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {
    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(WeatherRemoteDataSource.ROOT_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    @Provides
    fun provideGeocodingRemoteDataSource(retrofit: Retrofit): GeocodingRemoteDataSource {
        return retrofit.create(GeocodingRemoteDataSource::class.java)
    }

    @Provides
    fun provideWeatherRemoteDataSource(retrofit: Retrofit): WeatherRemoteDataSource {
        return retrofit.create(WeatherRemoteDataSource::class.java)
    }

    // TODO: Doesn't seem to work with current versions of protobuf and hilt
//    @Provides
//    fun provideWeatherDataStore(@ApplicationContext context: Context): DataStore<WeatherPb> {
//        return context.weatherStore
//    }
}