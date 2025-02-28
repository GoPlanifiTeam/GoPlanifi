# Documento de Diseño

## Descripción General

Este documento describe la arquitectura y el diseño del sistema, incluyendo sus principales entidades, atributos, métodos y relaciones.

## Entidades y sus Responsabilidades

| Entidad                    | Atributos                                                                                                                                         | Métodos                                                      |
|----------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------|
| **Autenticación**          | `User userId`, `Int loginErrors`                                                                                                                  | `login(User) : Boolean`<br>`logout(User) : Boolean`<br>`resetPassword(User) : Boolean` |
| **Usuario**                | `String userId`, `String email`, `String password`, `String firstName`, `String lastName`, `List<Trip> trips`, `String imageURL`                   | -                                                            |
| **Preferencias**           | `User userId`, `Boolean notificationsEnabled`, `String preferredLanguage`, `String theme`                                                         | `updatePreferences(String, String, Boolean)`                 |
| **Viaje**                  | `Map map`, `String id`, `User user`, `String destination`, `List<ItineraryItem> itineraries`, `String startDate`, `String endDate`, `List<Image> images`, `List<AIRecommendations> aiRecommendations` | - |
| **Mapa**                   | `Double latitude`, `Double longitude`, `String direction`                                                                                          | `showLocation() : String`<br>`getNearbyPlaces() : String`    |
| **Elemento de Itinerario** | `Trip trip`, `String id`, `String name`, `String location`, `String startDate`, `String endDate`                                                   | -                                                            |
| **Imagen**                 | `Trip trip`, `Int id`, `String imageURL`                                                                                                          | -                                                            |
| **Recomendaciones por IA** | `Trip trip`, `List<String> recommendations`                                                                                                      | -                                                            |


### 1. Autenticación

Maneja la autenticación de usuarios, incluyendo el inicio de sesión, cierre de sesión y restablecimiento de contraseña.

#### Atributos:

- `User userId`
- `Int loginErrors`

#### Métodos:

- `login(User) : Boolean`
- `logout(User) : Boolean`
- `resetPassword(User) : Boolean`

### 2. Usuario

Representa un usuario en el sistema, almacenando información personal y los viajes asociados.

#### Atributos:

- `String userId`
- `String email`
- `String password`
- `String firstName`
- `String lastName`
- `List<Trip> trips`
- `String imageURL`

### 3. Preferencias

Almacena las preferencias del usuario, como el idioma y el tema.

#### Atributos:

- `User userId`
- `Boolean notificationsEnabled`
- `String preferredLanguage`
- `String theme`

#### Métodos:

- `updatePreferences(String, String, Boolean)`

### 4. Viaje

Representa un viaje, incluyendo el destino, el itinerario, imágenes y recomendaciones generadas por IA.

#### Atributos:

- `Map map`
- `String id`
- `User user`
- `String destination`
- `List<ItineraryItem> itineraries`
- `String startDate`
- `String endDate`
- `List<Image> images`
- `List<AIRecommendations> aiRecommendations`

### 5. Mapa

Almacena los detalles geográficos de un viaje.

#### Atributos:

- `Double latitude`
- `Double longitude`
- `String direction`

#### Métodos:

- `showLocation() : String`
- `getNearbyPlaces() : String`

### 6. Elemento de Itinerario

Representa un elemento dentro del itinerario de un viaje.

#### Atributos:

- `Trip trip`
- `String id`
- `String name`
- `String location`
- `String startDate`
- `String endDate`

### 7. Imagen

Representa una imagen asociada con un viaje.

#### Atributos:

- `Trip trip`
- `Int id`
- `String imageURL`

### 8. Recomendaciones por IA

Almacena recomendaciones generadas por inteligencia artificial para un viaje.

#### Atributos:

- `Trip trip`
- `List<String> recommendations`

## Relaciones

- `User` puede tener múltiples objetos `Trip`.
- `Trip` contiene `Map`, `ItineraryItem`, `Image` y `AIRecommendations`.
- `ItineraryItem` está vinculado a un `Trip`.
- `Preferences` y `Authentication` están relacionadas con `User`.