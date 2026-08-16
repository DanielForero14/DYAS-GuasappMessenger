# 💬 GuasappMessenger — Decorator Pattern

## Patrones Estructurales — Taller 2

**Autor:** Daniel Felipe Forero Sánchez

---

## 📌 Descripción

**GuasappMessenger** es un simulador de mensajería en el que se implementaron diferentes mecanismos de protección sobre un `MessagingClient` proporcionado por terceros.

El objetivo fue agregar estas validaciones **sin modificar la lógica original de `MessagingClient`**, utilizando el patrón estructural **Decorator**.

Las validaciones implementadas son:

* 🛡️ **Contenido peligroso:** bloquea mensajes que contengan el patrón `##{...}`.
* 📏 **Longitud máxima:** bloquea mensajes de más de 200 caracteres.
* ⏱️ **Frecuencia de envío:** bloquea a partir del cuarto mensaje enviado en menos de 1 segundo.

Los mensajes que superan todas las validaciones son enviados normalmente por `MessagingClient`.

---

## 🎨 Patrón Decorator

Se utilizó el patrón **Decorator** para agregar validaciones alrededor del cliente original sin modificarlo.

Todos los componentes trabajan mediante la interfaz:

```java
MessageSender
```

La estructura implementada es:

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

Cada decorador tiene una responsabilidad específica y puede delegar el mensaje al siguiente componente de la cadena.

---

## 🔗 Cadena de validaciones

Las validaciones se combinan en `GuasappProgramLauncher`:

```java
MessageSender secureSender =
        new FrequencyValidator(
                new MaxLengthValidator(
                        new DangerousContentValidator(originalClient)
                )
        );
```

Por lo tanto, el flujo general es:

```text
Mensaje
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
Envío
```

Si alguna validación falla, el mensaje se bloquea y no continúa hacia `MessagingClient`.

---

## 🛡️ Validaciones implementadas

### Contenido peligroso

`DangerousContentValidator` detecta mensajes con el patrón:

```text
##{...}
```

Cuando encuentra este contenido registra:

```text
Mensaje bloqueado debido a contenido peligroso
```

### Longitud máxima

`MaxLengthValidator` permite mensajes de hasta **200 caracteres**.

Cuando se supera el límite registra:

```text
Mensaje bloqueado por exceder la longitud máxima permitida
```

### Frecuencia

`FrequencyValidator` permite máximo **3 mensajes dentro de una ventana de 1 segundo**.

A partir del cuarto mensaje registra:

```text
Mensaje bloqueado por exceso de frecuencia de envío
```

---

## 🧠 Decisiones de diseño

La principal decisión fue mantener `MessagingClient` separado de las reglas de seguridad.

Cada validación fue implementada como un decorador independiente utilizando `MessageSender`, evitando mezclar responsabilidades.

Esto permite:

* Mantener `MessagingClient` sin las nuevas reglas de validación.
* Combinar las validaciones mediante composición.
* Evitar duplicación de lógica.
* Mantener cada validación independiente.
* Agregar nuevas validaciones sin modificar las existentes.

La idea principal puede resumirse como:

```text
MessagingClient
       +
  Decoradores
       =
Envío protegido
```

---

## 🛠️ Compilación

Desde la carpeta raíz del proyecto:

```powershell
mvn clean compile
```

Una compilación correcta debe finalizar con:

```text
BUILD SUCCESS
```

---

## ▶️ Ejecución

Para ejecutar la aplicación:

```powershell
mvn exec:java "-Dexec.mainClass=edu.unisabana.dyas.patterns.GuasappProgramLauncher"
```

Un mensaje permitido llegará al cliente:

```text
Sending message: Hola, ¿cómo estás?
```

Mientras que un mensaje que incumpla alguna regla será bloqueado por el decorador correspondiente.

---

## 📝 Conclusión

La implementación del patrón **Decorator** permitió agregar diferentes capas de seguridad a `MessagingClient` sin incorporar todas las validaciones dentro de la clase original.

Cada regla mantiene una responsabilidad independiente y todas trabajan mediante `MessageSender`, permitiendo construir una solución desacoplada y fácil de mantener.

---

## 👨‍💻 Autor

**Daniel Felipe Forero Sánchez**

> **Un cliente de mensajería, diferentes capas de protección y Decorator como mecanismo para combinarlas.**
