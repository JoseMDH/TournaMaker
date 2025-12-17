TournaMaker es una aplicación móvil nativa para Android que permite crear y gestionar torneos y partidos de forma flexible.  

Tecnologías utilizadas:
- Android Studio y Kotlin
- Arquitectura MVVM
- Firebase Firestore

Diseño de la aplicación:
- Mockups y prototipos en Figma que hicimos el año pasado:  https://www.figma.com/design/J5f2eMHdbP3nTAhmTqZKGd/PAMN-Project?node-id=0-1&p=f&t=L7QfU8vzb95kfCWP-0
El diseño final se ha basado en esos prototipos, cambiando a los colores violeta/blanco para mejor contraste y accesibilidad y algunos otros detalles que nos parecieron convenientes.

Estructura del proyecto:
src/
main/
java/com/example/tournamaker/
data/
model/ # Clases de dominio (User, Team, Tournament, Match, Notification, ...)
repository/ # Repositorios para acceder a Firestore
ui/
auth/ # LoginFragment, RegisterFragment
main/ # Landing
team/ # Listado, creación y detalle de equipos
tournament/ # Listado, creación, detalle y bracket de torneos
match/ # Listado, creación y detalle de partidos
notification/ # Pantalla de notificaciones
user/ # Perfil y datos personales
viewmodel/ # ViewModels por funcionalidad
res/
layout/ # XML de los fragmentos y actividades
navigation/ # nav_graph.xml
values/ # strings.xml, colors.xml, themes.xml

Funcionalidades implementadas:
- Registro de usuario, verificación de correo e inicio/cierre de sesión.
- Gestión de perfil: datos personales, equipos creados, torneos y partidos en los que participa y estadísticas básicas.
- Creación, edición y listado de equipos sin límite de jugadores.
- Creación, edición y listado de torneos con nombre, descripción, fecha, lugar, premio, tasa de inscripción y número máximo de equipos.
- Creación, edición y listado de partidos con fecha, hora, lugar y marcador.
- Unión de equipos a torneos y partidos y de usuarios a equipos.
- Generación automática de un bracket de eliminatorias y avance de equipos según resultados.
- Sistema de notificaciones internas para informar de nuevas uniones.

Limitaciones conocidas y trabajo futuro:
- No se almacenan todavía estadísticas detalladas de jugadores (goles, asistencias, tarjetas) ni estadísticas avanzadas de equipo.
- Solo se soporta un idioma (español), aunque el proyecto está preparado para internacionalización mediante recursos de texto.
- La validación se ha basado principalmente en pruebas manuales; sería recomendable añadir una batería de pruebas unitarias e instrumentadas.
- Futuras mejoras posibles:
- Nuevos formatos de competición (ligas largas, fases de grupos, sistemas de puntuación configurables).
- Módulo de estadísticas completas por jugador y equipo.

Todo esto se explica mejor en la memoria, que está como pdf pero también se puede encontrar aquí: https://www.overleaf.com/read/xydyjjvrtdqt#9d43f7

Autores:
- José Manuel Díaz Hernández  
- Francisco Malillos Castellano
