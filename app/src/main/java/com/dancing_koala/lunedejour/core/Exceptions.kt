package com.dancing_koala.lunedejour.core

abstract class AppException(message: String) : Exception(message)

abstract class DbException(message: String) : AppException(message)
class EntityNotFoundException(key: String) : DbException("Entity not found for key: $key")