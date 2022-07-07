[![Build](https://github.com/anti-social/elasticmagic-kt/actions/workflows/build.yaml/badge.svg)](https://github.com/anti-social/elasticmagic-kt/actions/workflows/build.yaml)
[![Codecov](https://codecov.io/gh/anti-social/elasticmagic-kt/branch/master/graph/badge.svg?token=ELH5YR0I9C)](https://codecov.io/gh/anti-social/elasticmagic-kt)
[![Maven Central](https://img.shields.io/maven-central/v/dev.evo.elasticmagic/elasticmagic)](https://maven-badges.herokuapp.com/maven-central/dev.evo.elasticmagic/elasticmagic)
[![Documentation](https://img.shields.io/badge/Documentation-latest-orange)](https://anti-social.github.io/elasticmagic-kt/)
[![GitHub Discussions](https://img.shields.io/github/discussions/anti-social/elasticmagic-kt?label=Ask%20a%20question)](https://github.com/anti-social/elasticmagic-kt/discussions/categories/q-a)

# Elasticmagic for Kotlin

Elasticsearch query DSL for Kotlin

Read [User Guide](https://anti-social.github.io/elasticmagic-kt/document/)

## Features

- Multiplatform (JVM, Native and JS)
- Asynchronous API
- No reflection (only for delegates)
- Sub-fields and sub-documents with navigation
- Typed query expression (when it is possible)
- Typed document source
- Parent child relations (ability to merge multiple documents into a single mapping)
- Focused on a search API
- Declarative search query modifications via `query filters`
- Supports several serialization libraries (`kotlinx.serialization` and `jackson` out of the box)
- Supports Elasticsearch `6.x`, `7.x` and `8.x` (autodetect Elasticsearch version)
- Supports Opensearch `1.x` and `2.x`

## Not a goal

- Covering full Elasticsearch API
