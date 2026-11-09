# Decisiones de diseño — Semana 3

## 1. Punto de entrada

El proyecto mantiene un único punto de entrada:

```text
IngestaSensores.main()
```

No se crean aplicaciones independientes por semana.

`BancoDePruebas` es una clase auxiliar y no contiene `main`.

## 2. Búsqueda por timestamp

Se utilizan dos estrategias:

- Búsqueda lineal: no requiere ordenamiento.
- Búsqueda binaria: requiere que el arreglo esté ordenado por timestamp.

Los datos sintéticos de `GeneradorDatos` se generan en orden cronológico, por lo que la búsqueda binaria por timestamp cumple su precondición.

## 3. Búsqueda por PM2.5

No se asume que los datos estén ordenados por PM2.5.

Por tanto, la búsqueda binaria por PM2.5 se conserva como experimento para demostrar el efecto de una precondición incumplida.

## 4. Comparación de String

Los identificadores de estación se comparan mediante:

```java
equals()
```

y no mediante:

```java
==
```

porque se necesita comparar contenido.

## 5. Medición

La comparación principal entre algoritmos utiliza el número de comparaciones.

El tiempo en milisegundos se conserva como evidencia experimental, pero no es la única medida utilizada.

## 6. Evolución del proyecto

La Semana 3 agrega una nueva capacidad a la misma plataforma:

```text
Sensores
   ↓
Ingesta
   ↓
Repositorio
   ↓
Búsqueda
   ↓
Medición de eficiencia
```

La Semana 4 podrá extender esta misma arquitectura para estudiar ordenamiento.


# Decisiones de diseño — Semana 5

## 7. Lista enlazada como segunda implementación del contrato

`RepositorioEnlazado` implementa la misma interfaz `Repositorio` que
`RepositorioArreglo`. El código que usa un `Repositorio` no cambia al
reemplazar la implementación (Experimento 1 de `BancoDeListas`).

## 8. Insertar al inicio

Se prefiere la lista enlazada. Insertar al inicio solo cambia un número
constante de enlaces (0 saltos medidos), mientras el arreglo desplaza los
elementos existentes.

| Lecturas | Arreglo (movimientos) | Lista (saltos) |
|---:|---:|---:|
| 10.000 | 49.995.000 | 0 |
| 50.000 | 1.249.975.000 | 0 |
| 100.000 | 4.999.950.000 | 0 |

> Los contadores no dependen del equipo. Los milisegundos sí: agrega aquí
> los tuyos, medidos en tu computador.

## 9. Acceso por posición

Se prefiere el arreglo. `obtener(i)` en la lista recorre desde `primero`,
así que un ciclo `for (i...) obtener(i)` cuesta O(n²).

| Lecturas | Arreglo (accesos) | Lista por posición (saltos) | Lista nodo a nodo (saltos) |
|---:|---:|---:|---:|
| 1.000 | 1.000 | 499.500 | 999 |
| 10.000 | 10.000 | 49.995.000 | 9.999 |
| 50.000 | 50.000 | 1.249.975.000 | 49.999 |

Regla adoptada: para recorrer una lista completa se usa
`recorrerNodoPorNodo()` (un nodo `actual` que avanza una sola vez, O(n)) y
nunca un ciclo que llame a `obtener(i)`.

## 10. Búsqueda binaria sobre la lista

No se usa. Con 50.000 lecturas la binaria hace 16 comparaciones, pero llegar
a cada posición media cuesta saltos: 749.990 saltos contra 50.000 de la
búsqueda lineal. La estructura y el algoritmo deben elegirse en conjunto.

## 11. Eliminación y consistencia

`eliminar()` puentea el nodo (`anterior.setSiguiente(siguiente)`) y actualiza
`primero`, `ultimo` y `cantidad` en cada caso límite. La consistencia se
verifica comparando `tamano()` con `contarRecorriendo()` en
`RepositorioEnlazadoTest`.

## 12. Lista doble para el historial

`HistorialNavegable` usa una lista doblemente enlazada para retroceder sin
volver al principio. Costo: un enlace extra por nodo y el deber de mantener
`siguiente` y `anterior` sincronizados al agregar.

## 13. Qué estructura elegir para el histórico de lecturas

- Si predominan las inserciones al inicio: lista enlazada.
- Si predominan las consultas por posición: arreglo.