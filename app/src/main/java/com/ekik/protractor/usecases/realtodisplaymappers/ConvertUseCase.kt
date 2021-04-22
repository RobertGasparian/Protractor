package com.ekik.protractor.usecases.realtodisplaymappers

interface ConvertUseCase<T, E> {
    fun convert(value: T): E
}