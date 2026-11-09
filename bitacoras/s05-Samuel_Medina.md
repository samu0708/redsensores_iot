# Bitacora individual - Semana 05


## 1. Datos de la actividad

- **Estudiante:** Samuel Medina
- **Equipo:** Samuel
- **Semana:** 5
- **Fecha del laboratorio:** 9/11/26
- **Fecha del taller:** 9/11/26
- **Tema principal:** Listas enlazadas simples y dobles; comparacion con el arreglo.
- **Pregunta de la semana:** ¿Que estructura conviene segun las operaciones que predominan: insertar, acceder por posicion o recorrer?

## 2. Prediccion antes de ejecutar

1. **Que creo que va a ocurrir?**
   Crei que insertar 100.000 lecturas al inicio costaria parecido en el arreglo
   y en la lista enlazada, porque en los dos casos solo estoy "agregando un
   dato". No pensaba en lo que le pasa a los demas elementos.

2. **Que parte del programa o del algoritmo puede fallar?**
   La lista doble (`HistorialNavegable`). No tenia claro si al agregar al final
   tambien se actualizaba el enlace `anterior`; si faltaba, recorrer hacia atras
   no deberia visitar todos los nodos.

3. **Como comprobare mi prediccion?**
   Comparando los movimientos del arreglo con los saltos de la lista en el
   Experimento 3, y agregando 4 o 5 lecturas al historial para ver si
   `recorrerHaciaAtras()` da el mismo numero que `recorrerHaciaAdelante()`.

## 3. Evidencia del laboratorio

### Resultado observado (borrador)

Ejecute `BancoDeListas` con los 5 experimentos activados. Los contadores no
dependen del computador; los milisegundos si.

**Experimento 3 - insertar al inicio**

| Lecturas | Arreglo (movimientos) | Lista (saltos) | ms arreglo | ms lista |
|---:|---:|---:|-----------:|---------:|
| 10.000 | 49.995.000 | 0 |        171 |        0 |
| 50.000 | 1.249.975.000 | 0 |      1.846 |        0 |
| 100.000 | 4.999.950.000 | 0 |           7.567 |        0 |

**Experimento 4 - recorrer**

| Lecturas | Arreglo (accesos) | Lista con `obtener(i)` (saltos) | Lista nodo a nodo (saltos) |
|---:|---:|---:|---:|
| 1.000 | 1.000 | 499.500 | 999 |
| 10.000 | 10.000 | 49.995.000 | 9.999 |
| 50.000 | 50.000 | 1.249.975.000 | 49.999 |

**Experimento 5 - busqueda sobre la lista (50.000 lecturas, buscando la ultima)**

| Busqueda | Comparaciones | Saltos entre nodos |
|---|---:|---:|
| Lineal | 50.000 | 50.000 |
| Binaria | 16 | 749.990 |

**Experimentos 1 y 2:** el mismo ciclo funciono con ambas implementaciones y,
tras `eliminar(5)` sobre 10 lecturas, `tamano()` y `contarRecorriendo()` dieron 9.

**Pruebas:** corri `RepositorioEnlazadoTest`, `HistorialNavegableTest` y
`NodoTest`.

### Diferencia entre la prediccion y el resultado

Me equivoque en la primera. Predije que serian parecidos y no lo fueron: con
100.000 lecturas el arreglo hizo 4.999.950.000 movimientos y la lista 0 saltos.
No habia tenido en cuenta que el arreglo debe correr todos los elementos una
posicion para hacer sitio al inicio. En la segunda acerte a medias: el enlace
`anterior` si estaba actualizado y `recorrerHaciaAtras()` dio el mismo numero
que `recorrerHaciaAdelante()`, pero la prueba me sirvio para entender por que
hacia falta.

### Error o comportamiento inesperado (borrador)

- **Que ocurrio?** La busqueda binaria sobre la lista hizo 16 comparaciones pero
  costo 749.990 saltos, 15 veces mas que la lineal (50.000).
  
- **Por que ocurrio?** Cada comparacion de la binaria llama a `obtener(medio)`,
  que parte desde `primero` y camina hasta esa posicion. Reducir comparaciones no
  reduce el trabajo si llegar a cada posicion es caro.
- **Como lo corregimos o que falta corregir?** No se "corrige": se elige otra
  combinacion. Sobre lista se usa busqueda lineal; la binaria se reserva para el
  arreglo.

## 4. Explicacion en lenguaje llano (borrador)

> Un arreglo es una fila de casilleros numerados: puedo ir directo al casillero
> 500, pero para meter algo al principio tengo que correr a todos un casillero.
> Una lista enlazada es una cadena de cajas donde cada una dice cual es la
> siguiente: meter una caja al principio es solo cambiar a donde apunta la
> primera, pero para llegar a la caja 500 tengo que seguir la cadena desde el
> inicio.

### Ejemplo o analogia

El arreglo es una fila de sillas numeradas en un teatro: puedo ir directo a la
silla 500, pero si alguien quiere sentarse primero, todos se corren una silla.
La lista enlazada es una caceria del tesoro: cada pista dice donde esta la
siguiente, asi que poner una pista nueva al comienzo es solo cambiar a que
pista apunta la salida, pero para llegar a la pista 500 debo seguir las 499
anteriores. La analogia deja de ser exacta porque en la caceria las pistas
suelen ir en un solo sentido, mientras que en la lista doble cada nodo tambien
sabe cual es el anterior.

## 5. El vacio que encontre

- **Mi duda concreta es:** en la lista doble, ¿por que al agregar al final hay
  que escribir dos lineas (`ultimo.setSiguiente(nuevo)` y
  `nuevo.setAnterior(ultimo)`) y que pasa si falta la segunda?
- **Lo que ya puedo explicar es:** como funciona la lista simple: cada nodo guarda
  el dato y el enlace `siguiente`, y `ultimo` evita recorrer toda la lista para
  agregar al final.
- **Para resolver la duda consulte:** la seccion 5.6 de la guia, el codigo de
  `HistorialNavegable.java` y la prueba `testBackwardTraversalAfterAdding`.
- **Ahora lo entiendo asi:** cada nodo tiene que conocer a sus dos vecinos. Con
  solo `siguiente`, el nodo viejo sabe quien viene despues, pero el nuevo no
  sabe quien esta antes. Si se olvida `setAnterior`, el recorrido hacia adelante
  funciona, pero desde `ultimo` hacia atras solo se visita un nodo.

## 6. Trazado de la solucion (borrador)

Caso: lista `A → B → C → D` y se ejecuta `eliminar(2)` (el nodo C).

| Paso | Estado de la estructura | Decision o resultado |
|---|---|---|
| 1 | `primero→A→B→C→D→null`, `ultimo=D`, `cantidad=4` | Estado inicial |
| 2 | `posicion=2`, valida (0 ≤ 2 < 4) y distinta de 0 | Se busca el nodo anterior |
| 3 | El ciclo avanza 1 vez: `anterior=B` (1 salto) | B es el predecesor de C |
| 4 | `aEliminar=C`, `siguiente=D` | Se guardan los dos antes de reconectar |
| 5 | `B.setSiguiente(D)` → `A→B→D` | Se puentea C |
| 6 | `siguiente≠null`, asi que `ultimo` sigue en D; `cantidad=3` | `contarRecorriendo()` tambien da 3 |

**El defecto de la guia:** si en el paso 5 se escribe `B.setSiguiente(null)`, la
cadena queda `A→B→null`. C y D ya no se alcanzan desde `primero`, pero
`cantidad` dice 3 cuando hay 2 nodos reales. Las pruebas
`testEliminarDelMedioNoPierdeLosNodosPosteriores` y
`testTamanoCoincideConContarRecorriendoTrasCadaOperacion` lo detectan.

## 7. Decision de diseño (borrador)

- **Problema que debiamos resolver:** guardar el historico de lecturas del que
  dependen consultas y limpiezas, sabiendo que cada estructura reparte el costo
  de forma distinta.
- **Estructura elegida:** lista enlazada cuando predominan las inserciones al
  inicio y los recorridos completos nodo a nodo; arreglo cuando predominan las
  consultas por posicion.
- **Alternativa descartada:** busqueda binaria sobre la lista enlazada.
- **Por que elegimos la primera:** insertar al inicio en la lista no mueve datos
  (0 saltos); el costo es que acceder a una posicion exige caminar la cadena y
  que cada nodo guarda un enlace extra.
- **Que evidencia respalda la decision:** Experimentos 3, 4 y 5 (tablas de la
  seccion 3) y las 12 pruebas nuevas de `RepositorioEnlazadoTest`.

## 8. Aporte al proyecto (borrador)

- **Archivo(s) trabajado(s):** `src/repository/RepositorioEnlazado.java`,
  `src/experiment/BancoDeListas.java`, `test/repository/RepositorioEnlazadoTest.java`,
  `docs/decisiones.md`.
- **Cambio realizado:** se agrego `recorrerNodoPorNodo()`, se activaron los 5
  experimentos de `BancoDeListas` (el 4 compara tres formas de recorrer), se
  escribieron pruebas de casos limite de `eliminar()` y consistencia, y se
  documentaron las decisiones.
- **Como se conecta con la capa anterior:** `RepositorioEnlazado` implementa el
  mismo contrato `Repositorio` de la Semana 2, asi que reemplaza al arreglo sin
  cambiar el codigo que lo usa; los datos vienen de `GeneradorDatos`.
- **Que queda pendiente para la siguiente semana:** pasar a la Semana 6 (pilas,
  colas y monticulos) y aprovechar los nodos de esta semana para construirlas.
  Tambien queda por implementar `GestorDeRed`, que segun el README corresponde
  a la Semana 7.

## 9. Commits realizados

| Commit | Mensaje | Que demuestra |
|---|---|---|
| `[hash]` | `feat: agregar recorrido nodo a nodo a la lista enlazada` | Recorrido O(n) con contador de saltos |
| `[hash]` | `feat: activar experimentos 2 a 5 y comparar recorridos` | Mediciones de los 5 experimentos |
| `[hash]` | `test: agregar casos limite de RepositorioEnlazado` | Pruebas de eliminar y consistencia |
| `[hash]` | `docs: registrar decisiones de la semana 5` | Decisiones y tablas en `decisiones.md` |

## 10. Reexplicacion final (borrador)

> No existe una estructura mejor para todo: la lista enlazada gana al insertar
> al inicio (0 movimientos contra miles de millones del arreglo) y el arreglo
> gana al acceder por posicion. Elegi segun la operacion que predomina, y
> recorro la lista con un nodo `actual` en vez de `obtener(i)` para no pagar
> O(n²).

## 11. Reflexion individual

1. **Lo que ahora puedo hacer y antes no podia:**
   Recorrer una lista con un nodo `actual` y medir cuantos saltos cuesta, y
   escribir pruebas que comprueban casos limite en vez de solo imprimir resultados.
2. **El error o supuesto que mas me enseño:**
   Suponer que insertar al inicio costaba parecido en las dos estructuras. Los
   contadores (0 contra 4.999.950.000) me mostraron que lo que importa es cuantos
   datos hay que mover, no que la operacion se llame igual.
3. **La pregunta que llevaria a la proxima clase:**
   ¿Cuando vale la pena el enlace extra de la lista doble frente a una lista
   simple?
4. **Que parte del trabajo fue realmente mia:**
  TODO
## 12. Preguntas de la guia de la Semana 5 (borrador)

**1. ¿Por que insertar al inicio es mas economico en una lista enlazada que en un arreglo?**
En la lista solo se crea un nodo y se cambian dos referencias (`nuevo.siguiente`
y `primero`), sin importar cuantos datos haya. El arreglo debe correr cada
elemento una posicion a la derecha: con 100.000 lecturas fueron 4.999.950.000
movimientos.

**2. ¿Por que el acceso a una posicion arbitraria es mas costoso en una lista?**
Los nodos no estan uno junto al otro, asi que no hay una cuenta para saltar a la
posicion i. Hay que partir de `primero` y seguir los enlaces uno por uno: hasta
n-1 saltos. En el arreglo el indice lleva directo a la casilla.

**3. ¿Que diferencia existe entre un experimento de rendimiento y una prueba unitaria?**
El experimento mide cuanto cuesta algo (movimientos, saltos, tiempo) y sirve
para comparar estructuras; no dice si el codigo es correcto. La prueba unitaria
comprueba que un metodo cumple una regla ante una entrada conocida y falla si no
la cumple. Un programa puede ser rapido y estar mal, o correcto y lento.

**4. ¿Que caso limite consideras mas importante probar en `eliminar()` y por que?**
La posicion invalida o la lista vacia. Si `eliminar(-1)`, `eliminar(99)` o
`eliminar(0)` sobre una lista vacia no se validan, el metodo intenta recorrer
nodos que no existen y lanza una excepcion que detiene el programa. En una red
con miles de estaciones, un indice equivocado es un caso realista. Lo correcto es
devolver `false` y dejar la lista exactamente igual. Lo cubren
`testEliminarEnListaVaciaDevuelveFalse` y
`testEliminarIndiceInvalidoNoCambiaLaLista`.

**5. Para el historico de lecturas, ¿que estructura elegirias si predominan las inserciones al inicio? ¿Y si predominan las consultas por posicion?**
Con inserciones al inicio, la lista enlazada: cada insercion es de costo
constante y no mueve datos. Con consultas por posicion, el arreglo: el acceso por
indice es directo, mientras que la lista exige caminar desde `primero`.

## Lista de verificacion antes de entregar

- [ ] Escribi la prediccion antes de consultar el resultado.
- [ ] Inclui evidencia concreta del laboratorio.
- [ ] Explique un concepto sin depender de jerga.
- [ ] Registre un vacio, una duda o un error real.
- [ ] Trace al menos un caso paso a paso.
- [ ] Justifique una decision del proyecto y una alternativa descartada.
- [ ] Registre mis commits y mi aporte individual.
- [ ] Deje claro que queda pendiente.
- [ ] Renombre el archivo con el formato `sXX-nombre.md`.
