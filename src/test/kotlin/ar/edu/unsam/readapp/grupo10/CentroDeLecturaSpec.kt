package ar.edu.unsam.readapp.grupo10

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.assertThrows
import java.time.LocalTime
import java.time.LocalDate

class CentroDeLecturaSpec : DescribeSpec({
    isolationMode = IsolationMode.InstancePerTest

    val usuario1 = Usuario()
    val usuario2 = Usuario()
    val usuario3 = Usuario()
    val usuario4 = Usuario()
    val fecha1 = LocalDate.now().plusDays(3)
    val fecha2 = LocalDate.now().plusDays(10)
    val fecha3 = LocalDate.now().plusDays(17)
    val fecha4 = LocalDate.now().plusDays(24)
    val fecha5 = LocalDate.now().plusDays(27)


    describe("Dado un centro de lectura..") {
        it("se verifica que ha expirado la publicación del centro de lectura por fechas vencidas") {
            //arrange
            val centroDeLectura = Particular(porcentajeASuperar = 0.1, maximoParticipantes = 2)
            val fechaA = LocalDate.now().minusDays(2)
            val fechaB = LocalDate.now().minusDays(1)
            //act
            centroDeLectura.fechas.add(fechaA)
            centroDeLectura.fechas.add(fechaB)
            //assert
            centroDeLectura.expiroPublicacion() shouldBe true
        }
    }
    describe("Dado un centro de lectura Particular...") {
        val particular = Particular(maximoParticipantes = 30, porcentajeASuperar = 0.1)

        it("Tiene 3 fechas en su lista") {
            //Act
            particular.apply {
                agregarFecha(fecha1)
                agregarFecha(fecha2)
                agregarFecha(fecha3)
            }
            //Assert
            particular.cantidadFechas() shouldBe 3
        }
        it("como no supera el porcentaje máximo de los parcipantes, no agrega ningún extra al costo de reserva") {
            //Act
            particular.apply {
                registrarReserva(usuario1)
                registrarReserva(usuario2)
                registrarReserva(usuario3)
            }
            //Assert
            particular.costoDeReserva() shouldBe 1000
        }

        it("tiene 4 participantes en su lista") {
            //act
            particular.apply {
                registrarReserva(usuario1)
                registrarReserva(usuario2)
                registrarReserva(usuario3)
                registrarReserva(usuario4)
            }
            //assert
            particular.cantidadParticipantes() shouldBe 4
        }

        it("superando el porcentaje de los parcipantes, agrega costo adicional al costo de reserva") {
            particular.apply {
                registrarReserva(usuario1)
                registrarReserva(usuario2)
                registrarReserva(usuario3)
                registrarReserva(usuario4)
            }
            particular.costoDeReserva() shouldBe 1500
        }

    }
    describe("Dado un centro de lectura Editorial..") {
        val editorial = Editorial(montoAAlcanzar = 4000, asisteElAutor = true)
        val autorBestSeller = Autor()
        val libroBestSeller = Libro(autor = autorBestSeller, ventasSemanales = 10000, ediciones = 3)
        val libroNoBestSeller = Libro(autor = autorBestSeller, ventasSemanales = 9999)

        it("si el autor asiste con libro best seller, el costo de reserva aumenta por porcentaje de ventas semanales") {
            //act
            editorial.libroDesignado = libroBestSeller
            //assert
            editorial.costoDeReserva() shouldBe 2800
        }
        it("si el autor asiste y el libro no es best seller, el costo de reserva aumenta por monto fijo") {
            //act
            editorial.libroDesignado = libroNoBestSeller
            //assert
            editorial.costoDeReserva() shouldBe 2000
        }
        it("para la misma editorial, la cantidad maxima de participantes sera") {
            //act
            editorial.libroDesignado = libroNoBestSeller
            //assert
            editorial.participantesMaximos() shouldBe 2
        }

        it("si en la editorial no asiste el autor,solo suma el adicional de editorial") {
            editorial.apply {
                montoAAlcanzar = 3000
                asisteElAutor = false
            }
            editorial.costoDeReserva() shouldBe 1800
        }
    }
    describe("Dado un centro de lectura Biblioteca..") {
        val biblioteca = Biblioteca(gastosFijos = 1000.0, metrosCuadrados = 6)
        it("el costo de la reserva con pocas fecha será") {
            //act
            biblioteca.apply {
                agregarFecha(fecha1)
                agregarFecha(fecha2)
                agregarFecha(fecha3)
                agregarFecha(fecha4)
            }
            //assert
            biblioteca.cantidadFechas() shouldBe 4
            biblioteca.costoDeReserva() shouldBe 1014
        }

        it("el costo de la reserva con muchas fecha será") {
            //Act
            biblioteca.apply {
                agregarFecha(fecha1)
                agregarFecha(fecha2)
                agregarFecha(fecha3)
                agregarFecha(fecha4)
                agregarFecha(fecha5)
            }
            //Assert
            biblioteca.cantidadFechas() shouldBe 5
            biblioteca.costoDeReserva() shouldBe 1015
        }
        it("el número de participantes máximos debe ser igual a los metros cuadrados") {
            biblioteca.participantesMaximos() shouldBe 6
        }
    }
})