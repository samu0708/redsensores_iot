import model.LecturaSensor;
import org.junit.jupiter.api.Test;
import repository.RepositorioEnlazado;

import static org.junit.jupiter.api.Assertions.*;

public class RepositorioEnlazadoTest {

    @Test
    public void testAgregarAlFinalUsesUltimo() {
        RepositorioEnlazado lista = new RepositorioEnlazado();
        // agregar varios elementos al final
        for (int i = 0; i < 5; i++) {
            LecturaSensor l = new LecturaSensor("EST-001", String.format("2026-03-02 %02d:00", i), 20.0, 50.0, i);
            lista.agregarAlFinal(l);
        }
        assertEquals(5, lista.tamano());
        // el último elemento debería ser el último insertado
        LecturaSensor ultimo = lista.obtener(4);
        assertEquals(4.0, ultimo.getPm25());
        // no debería haber saltos al agregar al final
        RepositorioEnlazado.reiniciarSaltos();
        assertEquals(0, RepositorioEnlazado.getSaltos());
    }

    @Test
    public void testEliminarFirstNode() {
        RepositorioEnlazado lista = new RepositorioEnlazado();
        LecturaSensor a = new LecturaSensor("EST-001", "t0", 20, 50, 0);
        LecturaSensor b = new LecturaSensor("EST-001", "t1", 21, 51, 1);
        LecturaSensor c = new LecturaSensor("EST-001", "t2", 22, 52, 2);
        lista.agregarAlFinal(a);
        lista.agregarAlFinal(b);
        lista.agregarAlFinal(c);
        assertTrue(lista.eliminar(0));
        assertEquals(2, lista.tamano());
        // el nuevo primero debe ser b
        LecturaSensor primero = lista.obtener(0);
        assertEquals(1.0, primero.getPm25());
    }

    @Test
    public void testEliminarMiddleNodeKeepsChain() {
        RepositorioEnlazado lista = new RepositorioEnlazado();
        for (int i = 0; i < 5; i++) {
            lista.agregarAlFinal(new LecturaSensor("EST-001", "t"+i, 20+i, 50+i, i));
        }
        assertTrue(lista.eliminar(2)); // elimina el elemento con pm25=2
        assertEquals(4, lista.tamano());
        // los elementos restantes deben estar en orden 0,1,3,4
        assertEquals(0.0, lista.obtener(0).getPm25());
        assertEquals(1.0, lista.obtener(1).getPm25());
        assertEquals(3.0, lista.obtener(2).getPm25());
        assertEquals(4.0, lista.obtener(3).getPm25());
    }

    @Test
    public void testEliminarLastNodeUpdatesUltimo() {
        RepositorioEnlazado lista = new RepositorioEnlazado();
        for (int i = 0; i < 3; i++) {
            lista.agregarAlFinal(new LecturaSensor("EST-001", "t"+i, 20+i, 50+i, i));
        }
        assertTrue(lista.eliminar(2)); // elimina el último
        assertEquals(2, lista.tamano());
        // el último elemento ahora debe ser el de índice 1
        assertEquals(1.0, lista.obtener(1).getPm25());
    }

    @Test
    public void testContarRecorriendoMatchesCantidad() {
        RepositorioEnlazado lista = new RepositorioEnlazado();
        for (int i = 0; i < 7; i++) {
            lista.agregarAlFinal(new LecturaSensor("EST-001", "t"+i, 20+i, 50+i, i));
        }
        assertEquals(7, lista.tamano());
        assertEquals(7, lista.contarRecorriendo());
    }

    // =========================================================
    //  SEMANA 5: casos limite y consistencia (tabla de la guia)
    // =========================================================

    private static LecturaSensor lectura(int i) {
        return new LecturaSensor("EST-001", "t" + i, 20 + i, 50 + i, i);
    }

    private static RepositorioEnlazado listaConN(int n) {
        RepositorioEnlazado lista = new RepositorioEnlazado();
        for (int i = 0; i < n; i++) lista.agregarAlFinal(lectura(i));
        return lista;
    }

    @Test
    public void testListaNuevaEstaVacia() {
        RepositorioEnlazado lista = new RepositorioEnlazado();
        assertEquals(0, lista.tamano());
        assertEquals(0, lista.contarRecorriendo());
        assertNull(lista.obtener(0));
    }

    @Test
    public void testEliminarEnListaVaciaDevuelveFalse() {
        RepositorioEnlazado lista = new RepositorioEnlazado();
        assertFalse(lista.eliminar(0));
        assertEquals(0, lista.tamano());
    }

    @Test
    public void testEliminarIndiceInvalidoNoCambiaLaLista() {
        RepositorioEnlazado lista = listaConN(3);
        assertFalse(lista.eliminar(-1));
        assertFalse(lista.eliminar(3));
        assertFalse(lista.eliminar(99));
        assertEquals(3, lista.tamano());
        assertEquals(3, lista.contarRecorriendo());
    }

    @Test
    public void testEliminarElUnicoNodoDejaListaVaciaYUtilizable() {
        RepositorioEnlazado lista = listaConN(1);
        assertTrue(lista.eliminar(0));
        assertEquals(0, lista.tamano());
        assertEquals(0, lista.contarRecorriendo());
        // primero y ultimo deben haber quedado en null: agregar de nuevo debe funcionar
        lista.agregarAlFinal(lectura(7));
        assertEquals(1, lista.tamano());
        assertEquals(7.0, lista.obtener(0).getPm25());
    }

    @Test
    public void testEliminarUltimoYLuegoAgregarAlFinalMantieneElOrden() {
        RepositorioEnlazado lista = listaConN(3);          // 0,1,2
        assertTrue(lista.eliminar(2));                      // 0,1
        lista.agregarAlFinal(lectura(9));                   // 0,1,9
        assertEquals(3, lista.tamano());
        assertEquals(3, lista.contarRecorriendo());
        assertEquals(0.0, lista.obtener(0).getPm25());
        assertEquals(1.0, lista.obtener(1).getPm25());
        assertEquals(9.0, lista.obtener(2).getPm25());
    }

    @Test
    public void testEliminarDelMedioNoPierdeLosNodosPosteriores() {
        RepositorioEnlazado lista = listaConN(6);          // 0..5
        assertTrue(lista.eliminar(2));
        // Debe desaparecer UNO solo: 5 nodos reales, no menos.
        assertEquals(5, lista.tamano());
        assertEquals(5, lista.contarRecorriendo());
        assertEquals(5.0, lista.obtener(4).getPm25());
    }

    @Test
    public void testAgregarAlInicioDejaLaNuevaLecturaPrimera() {
        RepositorioEnlazado lista = listaConN(2);          // 0,1
        lista.agregarAlInicio(lectura(8));                  // 8,0,1
        assertEquals(3, lista.tamano());
        assertEquals(8.0, lista.obtener(0).getPm25());
        assertEquals(1.0, lista.obtener(2).getPm25());
    }

    @Test
    public void testAgregarAlInicioEnListaVaciaTambienFijaUltimo() {
        RepositorioEnlazado lista = new RepositorioEnlazado();
        lista.agregarAlInicio(lectura(1));
        lista.agregarAlFinal(lectura(2));                   // exige que ultimo sea valido
        assertEquals(2, lista.contarRecorriendo());
        assertEquals(2.0, lista.obtener(1).getPm25());
    }

    @Test
    public void testTamanoCoincideConContarRecorriendoTrasCadaOperacion() {
        RepositorioEnlazado lista = new RepositorioEnlazado();
        lista.agregarAlFinal(lectura(0));
        assertEquals(lista.tamano(), lista.contarRecorriendo());
        lista.agregarAlInicio(lectura(1));
        assertEquals(lista.tamano(), lista.contarRecorriendo());
        lista.agregarAlFinal(lectura(2));
        assertEquals(lista.tamano(), lista.contarRecorriendo());
        lista.eliminar(1);
        assertEquals(lista.tamano(), lista.contarRecorriendo());
        lista.eliminar(0);
        assertEquals(lista.tamano(), lista.contarRecorriendo());
        lista.eliminar(0);
        assertEquals(lista.tamano(), lista.contarRecorriendo());
        assertEquals(0, lista.tamano());
    }

    @Test
    public void testRecorrerNodoPorNodoVisitaTodosConNMenosUnoSaltos() {
        RepositorioEnlazado lista = listaConN(100);
        RepositorioEnlazado.reiniciarSaltos();
        assertEquals(100, lista.recorrerNodoPorNodo());
        assertEquals(99, RepositorioEnlazado.getSaltos());
    }

    @Test
    public void testRecorrerNodoPorNodoEnListaVaciaNoFalla() {
        RepositorioEnlazado lista = new RepositorioEnlazado();
        RepositorioEnlazado.reiniciarSaltos();
        assertEquals(0, lista.recorrerNodoPorNodo());
        assertEquals(0, RepositorioEnlazado.getSaltos());
    }

    @Test
    public void testRecorrerPorPosicionCuestaMuchoMasQueNodoPorNodo() {
        RepositorioEnlazado lista = listaConN(200);

        RepositorioEnlazado.reiniciarSaltos();
        for (int i = 0; i < lista.tamano(); i++) lista.obtener(i);
        long porPosicion = RepositorioEnlazado.getSaltos();      // 0+1+...+199 = 19.900

        RepositorioEnlazado.reiniciarSaltos();
        lista.recorrerNodoPorNodo();
        long nodoANodo = RepositorioEnlazado.getSaltos();        // 199

        assertEquals(19_900, porPosicion);
        assertEquals(199, nodoANodo);
        assertTrue(porPosicion > nodoANodo);
    }
}