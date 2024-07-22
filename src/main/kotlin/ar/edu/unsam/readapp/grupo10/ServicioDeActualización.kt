package ar.edu.unsam.readapp.grupo10

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

interface ServicioDeActualizacion {
    fun getLibros(): String
}

class ActualizadorLibros(
    val servicio: ServicioDeActualizacion,
    val repositorioLibro: Repositorio<Libro>
) {
    val gson = Gson()

    fun actualizarRepositorio() {
        librosAActualizar().forEach { repositorioLibro.actualizar(it) }
    }

    fun parsearJson(): List<LibroJson> {
        return gson.fromJson(servicio.getLibros(), object : TypeToken<List<LibroJson>>() {}.type)
    }

    fun fromLibroJsonToLibro(libroJson: LibroJson): Libro {
        val libroEnRepositorio = repositorioLibro.obtenerPorId(libroJson.id)
        return Libro(
            palabras = libroEnRepositorio.palabras,
            paginas = libroEnRepositorio.paginas,
            ediciones = libroJson.ediciones,
            ventasSemanales = libroJson.ventasSemanales,
            lecturaCompleja = libroEnRepositorio.lecturaCompleja,
            autor = libroEnRepositorio.autor,
            lenguajeOriginal = libroEnRepositorio.lenguajeOriginal,
            titulo = libroEnRepositorio.titulo
        ).apply { asignarID(libroEnRepositorio.id) }
    }

    fun librosAActualizar(): List<Libro> {
        return parsearJson().map { libroJson ->
            fromLibroJsonToLibro(libroJson)
        }
    }
}

data class LibroJson(
    val id: Int,
    val ediciones: Int,
    val ventasSemanales: Int
)