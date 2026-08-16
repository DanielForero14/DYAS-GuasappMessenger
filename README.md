# 💬 GuasappMessenger — Secure Messaging

## Patrones Estructurales — Taller 2

**Autor:** Daniel Felipe Forero Sánchez

---

## 📌 Descripción del proyecto

**GuasappMessenger** es un simulador de una herramienta de mensajería que utiliza una clase externa llamada `MessagingClient` para realizar el envío de mensajes.

El objetivo del proyecto fue agregar diferentes mecanismos de protección alrededor de este cliente sin modificar su implementación original.

Para conseguirlo se utilizó el patrón de diseño estructural **Decorator**, permitiendo incorporar validaciones independientes antes de que un mensaje sea enviado.

Las validaciones implementadas son:

- 🛡️ **Contenido peligroso:** bloquea mensajes que contengan el patrón `##{...}`.
- 📏 **Longitud máxima:** bloquea mensajes que superen los 200 caracteres.
- ⏱️ **Frecuencia de envío:** permite máximo 3 mensajes dentro de una ventana de 1 segundo.

Si un mensaje supera todas las validaciones, finalmente es entregado al `MessagingClient`.

---

# 🎯 Objetivo

El proyecto parte de una clase externa:

```java
MessagingClient
```

Esta clase representa un componente proporcionado por terceros y su responsabilidad es enviar los mensajes:

```java
public void sendMessage(String message) {
    System.out.println("Sending message: " + message);
}
```

La intención de la solución fue mantener esta clase independiente de las nuevas reglas de seguridad.

En lugar de agregar todas las validaciones directamente dentro de `MessagingClient`, cada responsabilidad fue separada:

```text
MENSAJE
   │
   ▼
Validación de frecuencia
   │
   ▼
Validación de longitud
   │
   ▼
Validación de contenido
   │
   ▼
MessagingClient
   │
   ▼
ENVÍO
```

De esta manera, cada validación puede decidir si permite continuar el flujo o bloquea el mensaje.

---

# 🎨 Patrón de diseño: Decorator

El patrón **Decorator** permite agregar responsabilidades a un objeto de forma dinámica sin modificar su implementación original.

En este proyecto, todos los componentes trabajan mediante la interfaz:

```java
MessageSender
```

Esta interfaz representa cualquier objeto capaz de enviar un mensaje.

Conceptualmente:

```text
                MessageSender
                     ▲
                     │
          ┌──────────┴──────────┐
          │                     │
  MessagingClient     MessageSenderDecorator
                                ▲
                    ┌───────────┼───────────┐
                    │           │           │
             Dangerous      MaxLength    Frequency
              Content       Validator    Validator
             Validator
```

`MessagingClient` realiza el envío real, mientras los decoradores agregan las validaciones de seguridad.

---

# 🔌 Interfaz MessageSender

`MessageSender` funciona como la abstracción común de toda la solución.

Tanto `MessagingClient` como los decoradores pueden ser tratados como:

```java
MessageSender
```

Gracias a esto, un decorador puede envolver otro objeto que implemente la misma interfaz.

La estructura permite construir una cadena como:

```text
MessageSender
     │
     ▼
FrequencyValidator
     │
     ▼
MaxLengthValidator
     │
     ▼
DangerousContentValidator
     │
     ▼
MessagingClient
```

El mensaje atraviesa esta cadena antes de llegar al cliente original.

---

# 🧩 Decorador base

Para evitar repetir la lógica necesaria para envolver un `MessageSender`, se utiliza:

```java
MessageSenderDecorator
```

Esta clase funciona como base para las diferentes validaciones.

Conceptualmente:

```text
MessageSenderDecorator
        │
        └── MessageSender wrapped
```

Cada decorador concreto hereda esta estructura y solamente debe encargarse de implementar su propia regla.

Esto permite mantener separadas las responsabilidades y evita que una validación necesite conocer los detalles de las demás.

---

# 🛡️ Validación de contenido peligroso

La primera protección está implementada mediante:

```java
DangerousContentValidator
```

Su responsabilidad es detectar mensajes que contengan el patrón:

```text
##{...}
```

Por ejemplo:

```text
##{./exec(rm /* -r)}
```

Para detectar este contenido se utiliza una expresión regular equivalente al patrón esperado.

Cuando se encuentra contenido peligroso, el mensaje no continúa por la cadena y se registra:

```text
Mensaje bloqueado debido a contenido peligroso
```

Si el contenido es seguro, el mensaje se delega al siguiente componente.

```text
Mensaje
   │
   ▼
¿Contiene ##{...}?
   │
 ┌─┴─┐
Sí   No
│     │
▼     ▼
Bloqueo   Siguiente decorador
```

---

# 📏 Validación de longitud

La segunda protección está implementada mediante:

```java
MaxLengthValidator
```

El sistema establece una longitud máxima de:

```java
200 caracteres
```

Si:

```text
longitud <= 200
```

el mensaje puede continuar.

Si:

```text
longitud > 200
```

el mensaje es bloqueado y se registra:

```text
Mensaje bloqueado por exceder la longitud máxima permitida
```

Esta validación protege al sistema frente al procesamiento de mensajes excesivamente grandes.

---

# ⏱️ Validación de frecuencia

La tercera protección está implementada mediante:

```java
FrequencyValidator
```

Esta clase controla la cantidad de mensajes enviados dentro de una ventana de tiempo.

Los límites utilizados son:

```text
Máximo de mensajes: 3
Ventana de tiempo: 1 segundo
```

Los primeros tres mensajes dentro de la ventana pueden continuar normalmente.

A partir del cuarto mensaje enviado dentro del mismo intervalo, el sistema registra:

```text
Mensaje bloqueado por exceso de frecuencia de envío
```

Conceptualmente:

```text
       Mensaje
          │
          ▼
 ¿Cuántos mensajes se han
 enviado en el último segundo?
          │
      ┌───┴───┐
      │       │
    <= 3     > 3
      │       │
      ▼       ▼
 Continuar  Bloquear
```

Esto permite implementar una protección básica contra envíos excesivamente rápidos.

---

# 🔗 Composición de las validaciones

Una de las ventajas principales de **Decorator** es que las validaciones pueden combinarse.

En `GuasappProgramLauncher` se construye una cadena de decoradores alrededor del cliente original:

```java
MessageSender secureSender =
        new FrequencyValidator(
                new MaxLengthValidator(
                        new DangerousContentValidator(originalClient)
                )
        );
```

La estructura resultante es:

```text
FrequencyValidator
        │
        ▼
MaxLengthValidator
        │
        ▼
DangerousContentValidator
        │
        ▼
MessagingClient
```

Cuando se ejecuta:

```java
secureSender.sendMessage(message);
```

el mensaje comienza en el decorador exterior y avanza por la cadena.

Cada decorador tiene dos posibilidades:

```text
VALIDACIÓN CORRECTA
        │
        ▼
delegar al siguiente

VALIDACIÓN FALLIDA
        │
        ▼
bloquear el mensaje
```

De esta forma, `MessagingClient` solamente recibe mensajes que hayan superado las validaciones anteriores.

---

# 🧠 Separación de responsabilidades

Cada componente tiene una única responsabilidad.

```text
MessagingClient
      │
      └── Enviar mensajes

DangerousContentValidator
      │
      └── Validar contenido

MaxLengthValidator
      │
      └── Validar longitud

FrequencyValidator
      │
      └── Controlar frecuencia
```

Una validación no necesita conocer cómo funcionan las demás.

Por ejemplo:

```java
DangerousContentValidator
```

no necesita saber que existe:

```java
FrequencyValidator
```

y viceversa.

La relación entre ellas se consigue mediante:

```java
MessageSender
```

---

# 🔒 Protección de MessagingClient

Una decisión importante de diseño fue mantener las reglas de seguridad fuera de:

```java
MessagingClient
```

La clase continúa teniendo únicamente la responsabilidad de realizar el envío:

```text
MessagingClient
      │
      ▼
Sending message: ...
```

Las protecciones son aplicadas externamente mediante los decoradores.

Esto permite agregar comportamiento al cliente sin mezclar la lógica de envío con las reglas de validación.

---

# ♻️ Principio Abierto/Cerrado

La estructura también aplica el principio **Open/Closed Principle (OCP)**.

La solución está:

```text
ABIERTA PARA EXTENSIÓN
        +
CERRADA PARA MODIFICACIÓN
```

Esto significa que una nueva regla puede implementarse mediante otro decorador basado en:

```java
MessageSenderDecorator
```

sin modificar:

```text
MessagingClient
DangerousContentValidator
MaxLengthValidator
FrequencyValidator
```

Cada nueva responsabilidad puede mantenerse independiente de las anteriores.

---

# 🏗️ Arquitectura general

La arquitectura final puede resumirse de la siguiente manera:

```text
                     MessageSender
                          ▲
                          │
             ┌────────────┴────────────┐
             │                         │
      MessagingClient       MessageSenderDecorator
                                       ▲
                          ┌────────────┼────────────┐
                          │            │            │
                   Dangerous       MaxLength    Frequency
                    Content        Validator    Validator
                   Validator
```

Durante la ejecución:

```text
                    MENSAJE
                       │
                       ▼
              FrequencyValidator
                       │
                       ▼
              MaxLengthValidator
                       │
                       ▼
          DangerousContentValidator
                       │
                       ▼
               MessagingClient
                       │
                       ▼
                     ENVÍO
```

Si alguna validación falla, el recorrido se detiene y el mensaje no llega al cliente.

---

# 📂 Estructura principal del proyecto

La organización principal del proyecto es:

```text
src/main/java/edu/unisabana/dyas/patterns/
│
├── GuasappProgramLauncher.java
│
├── util/
│   ├── MessageSender.java
│   └── MessagingClient.java
│
└── decorator/
    ├── MessageSenderDecorator.java
    ├── DangerousContentValidator.java
    ├── MaxLengthValidator.java
    └── FrequencyValidator.java
```

Las responsabilidades quedan organizadas entre:

```text
util/
    → abstracción y cliente original

decorator/
    → reglas agregadas mediante Decorator

GuasappProgramLauncher
    → configuración y ejecución
```

---

# 🛠️ Requisitos

Para compilar y ejecutar el proyecto es necesario tener instalado:

- Java.
- Apache Maven.
- Git.
- Un IDE o editor compatible con Java, como Visual Studio Code.

Las instalaciones pueden comprobarse mediante:

```powershell
java -version
```

```powershell
mvn -version
```

```powershell
git --version
```

---

# ▶️ Compilación

Desde la carpeta raíz del proyecto, donde se encuentra:

```text
pom.xml
```

ejecutar:

```powershell
mvn clean compile
```

También puede utilizarse:

```powershell
mvn compile
```

Una compilación correcta debe finalizar con:

```text
BUILD SUCCESS
```

---

# 🚀 Ejecución

Para ejecutar la aplicación:

```powershell
mvn exec:java "-Dexec.mainClass=edu.unisabana.dyas.patterns.GuasappProgramLauncher"
```

Durante la ejecución, un mensaje válido puede producir:

```text
Sending message: Hola, ¿cómo estás?
```

Mientras que los mensajes que incumplan las reglas son detenidos por el decorador correspondiente.

Los posibles registros de bloqueo son:

```text
Mensaje bloqueado debido a contenido peligroso
```

```text
Mensaje bloqueado por exceder la longitud máxima permitida
```

```text
Mensaje bloqueado por exceso de frecuencia de envío
```

---

# 🧠 Lo que aprendimos / Decisiones de diseño

Durante el desarrollo se tomaron varias decisiones importantes.

### 1. Proteger el cliente sin modificarlo

La principal decisión fue mantener `MessagingClient` separado de las reglas de seguridad.

En lugar de introducir las validaciones dentro de esta clase, estas fueron implementadas como decoradores externos.

Esto permite conservar la responsabilidad original del cliente:

```text
MessagingClient → enviar mensajes
```

mientras otros componentes se encargan de:

```text
Validar → decidir → delegar
```

---

### 2. Utilizar una abstracción común

Todos los componentes trabajan mediante:

```java
MessageSender
```

Esto permite que tanto el cliente original como un decorador puedan utilizarse de la misma manera:

```java
sendMessage(...)
```

Gracias a esto es posible envolver decoradores dentro de otros decoradores.

---

### 3. Mantener independientes las validaciones

Cada regla fue implementada en una clase diferente:

```text
DangerousContentValidator
MaxLengthValidator
FrequencyValidator
```

Ninguna necesita conocer la implementación de las otras.

Cada una solamente:

```text
1. Recibe el mensaje.
2. Aplica su regla.
3. Bloquea o delega.
```

Esto mantiene el código organizado y evita mezclar diferentes responsabilidades dentro de una sola clase.

---

### 4. Componer comportamiento mediante Decorator

Las validaciones no necesitan ser controladas mediante estructuras como:

```java
if (tipoValidacion ...)
```

o:

```java
switch (tipoValidacion ...)
```

En su lugar, el comportamiento se construye mediante composición:

```java
new FrequencyValidator(
    new MaxLengthValidator(
        new DangerousContentValidator(
            originalClient
        )
    )
);
```

La estructura de objetos determina qué validaciones serán aplicadas.

---

# 📝 Conclusión

La implementación del patrón **Decorator** permitió agregar mecanismos de protección a `MessagingClient` sin incorporar estas responsabilidades dentro del cliente original.

La solución separa claramente:

```text
ENVÍO DE MENSAJES
        │
        ▼
 MessagingClient
```

de:

```text
VALIDACIONES
    │
    ├── Contenido peligroso
    ├── Longitud máxima
    └── Frecuencia
```

Cada regla funciona como un decorador independiente y todos los componentes trabajan mediante la interfaz `MessageSender`.

El resultado puede resumirse como:

```text
MESSAGING CLIENT
       +
  DECORADORES
       =
ENVÍO PROTEGIDO
```

De esta forma, las reglas pueden combinarse sin duplicar lógica, cada componente mantiene una responsabilidad específica y el sistema conserva una estructura flexible y desacoplada.

---

# 👨‍💻 Autor

**Daniel Felipe Forero Sánchez**

---

## 💬 GuasappMessenger — Decorator Pattern

> **Un cliente sencillo, múltiples capas de protección y una cadena de decoradores encargada de decidir qué mensajes pueden continuar.**