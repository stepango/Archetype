# Archetype
[![codebeat badge](https://codebeat.co/badges/15f6548e-a136-4c0d-9f53-460166070ce6)](https://codebeat.co/projects/github-com-stepango-archetype-master)
[![Build Status](https://www.bitrise.io/app/65613de01e0da309.svg?token=6-VjC_AlRy2-0Aq6W-OALw&branch=master)](https://www.bitrise.io/app/65613de01e0da309)

Badass MVVM architecture.

At the moment Archetype contains implementation of Android Dev podcast player. 

Official Telegram chat https://t.me/archetype_android

Mobius Russia 2017 Talk https://www.youtube.com/watch?v=M3fTMBfmBqU&t=1380s

# Main libraries and concepts
- Android SDK, JDK 1.8 and [Kotlin](https://kotlinlang.org/)
- [Reactive programming](http://reactivex.io/) with [RxJava2](https://github.com/ReactiveX/RxJava) for asynchronous tasks
- [Retrofit](https://github.com/square/retrofit) - for simple REST implementation

## Build
Project uses Gradle as build system. You can find main gradle config for Android app module here: `app/build.gradle`

# Code organisation rules:

## Basic
- All or no arguments should be named when pass to function, partial naming is not allowed

## Kotlin
- Order of declarations inside class or file: `val`, `var`, `constructor`, `init`, `fun`, `private fun`

## DataBindings
- All general function's annotated with `@BindingAdapter` should be stored in `*.databindings` package, filename should be `'ViewName'Bindings.kt`.
- `@BindingAdapter` functions that couldn't be reused should be stored in file that contains related VM or should be grouped in separate file named `'Feature'Bindings.kt`
- All all bindings in xml should start with `bind:` prefix
- All ViewModels in XML should be named `vm`

## Gradle
- All lib and gradle plugin versions should be stored in root `build.gradle` file.

## Rx
- Subscribing to observable allowed only with `subscribeBy` or `bindSubscribe` extension methods.
