package com.example.facultades.controller;

import com.example.facultades.dto.UniversidadDTO;
import com.example.facultades.excepciones.RegistroExistenteException;
import com.example.facultades.generics.ControllerGeneric;
import com.example.facultades.generics.GenericService;
import com.example.facultades.generics.IgenericService;
import com.example.facultades.model.Universidad;
import com.example.facultades.service.IUniversidadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.sound.sampled.Control;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/universidad")
public class UniversidadController extends ControllerGeneric<Universidad, UniversidadDTO, Long> {

    @Autowired
    private IUniversidadService universidadService;

    @Autowired
    private IgenericService<Universidad, Long> IuniversidadGenericService;

    @GetMapping("/obtenerTopUniversidades")
    @Operation(
            summary = "Obtener las universidades principales",
            description = "Este endpoint permite obtener las universidades principales, las cuales no han sido eliminadas lógicamente, con paginación y un tamaño de página configurable.",
            tags = {"Universidades"},
            parameters = {
                    @Parameter(
                            name = "pagina",
                            description = "Número de la página para la paginación de resultados. El valor por defecto es 0.",
                            required = false,
                            in = ParameterIn.QUERY,
                            example = "0"
                    ),
                    @Parameter(
                            name = "tamanio",
                            description = "Cantidad de universidades por página. El valor por defecto es 10.",
                            required = false,
                            in = ParameterIn.QUERY,
                            example = "10"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lista de universidades principales obtenida exitosamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UniversidadDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Solicitud incorrecta (por ejemplo, parámetros de paginación inválidos)"
                    )
            }
    )
    public ResponseEntity<List<UniversidadDTO>> obtenerPrimerasTresImagenes(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanio)

    {
        Pageable pageable = PageRequest.of(pagina, tamanio);
        List<Universidad> universidades = universidadService.obtenerUniversidadesPaginadas(pageable).getContent();
        List<UniversidadDTO> universidadDTOS = new ArrayList<>();
        for (Universidad universidad : universidades) {
            if (!universidad.isEliminacionLogica()) {
                universidadDTOS.add((UniversidadDTO) IuniversidadGenericService.convertirDTO(universidad));
            }
        }
        return new ResponseEntity<>(universidadDTOS, HttpStatus.OK);
    }


    //@PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/paginadas")
    @Operation(
            summary = "Obtener universidades paginadas",
            description = "Este endpoint permite obtener universidades de manera paginada.",
            tags = {"Universidades"},
            parameters = {
                    @Parameter(
                            name = "pagina",
                            description = "Número de la página para la paginación de resultados. El valor por defecto es 0.",
                            required = false,
                            in = ParameterIn.QUERY,
                            example = "0"
                    ),
                    @Parameter(
                            name = "tamanio",
                            description = "Cantidad de universidades por página. El valor por defecto es 10.",
                            required = false,
                            in = ParameterIn.QUERY,
                            example = "10"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lista de universidades paginadas obtenida exitosamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UniversidadDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Solicitud incorrecta (por ejemplo, parámetros de paginación inválidos)"
                    )
            }
    )
    public ResponseEntity<List<UniversidadDTO>> obtenerUniversidadesPaginadas(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanio)
    {
        List<UniversidadDTO> listaUniversidadDTO = new ArrayList<>();
        Pageable pageable = PageRequest.of(pagina, tamanio);

        Page<Universidad> universidades = universidadService.obtenerUniversidadesPaginadas(pageable);
        List<Universidad> listaUniversidades = universidades.getContent();

        for (Universidad universidad : listaUniversidades) {
         //  if(!universidad.isEliminacionLogica()){
               listaUniversidadDTO.add((UniversidadDTO) IuniversidadGenericService.convertirDTO(universidad));
           //}
        }
        System.out.printf("universidades " + listaUniversidadDTO.size());
        System.out.printf("universidades " + listaUniversidades.size());
        return new ResponseEntity<>(listaUniversidadDTO, HttpStatus.OK);
    }


    @GetMapping("/universidadID/{idCarrera}")
    @Operation(
            summary = "Obtener universidad por ID de carrera",
            description = "Este endpoint permite obtener una universidad asociada a una carrera mediante el ID de la carrera proporcionado.",
            tags = {"Universidades"},
            parameters = {
                    @Parameter(
                            name = "idCarrera",
                            description = "ID de la carrera para la cual se desea obtener la universidad asociada.",
                            required = true,
                            in = ParameterIn.PATH,
                            example = "1"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Universidad obtenida exitosamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Universidad.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Carrera no encontrada, no se encontró universidad asociada"
                    )
            }
    )
    public ResponseEntity<Universidad> getuniversidadIdCarrera(@PathVariable Long idCarrera){
        Universidad universidad = universidadService.getIDUniversidadPorCarreraId(idCarrera);
        return new ResponseEntity<>(universidad, HttpStatus.OK);
    }

    @GetMapping("/findUniversidadByName/{nombreUniversidad}")
    @Operation(
            summary = "Buscar universidades por nombre",
            description = "Este endpoint permite buscar universidades basándose en el nombre de la universidad.",
            tags = {"Universidades"},
            parameters = {
                    @Parameter(
                            name = "nombreUniversidad",
                            description = "Nombre de la universidad que se desea buscar.",
                            required = true,
                            in = ParameterIn.PATH,
                            example = "Universidad Nacional"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lista de universidades obtenida exitosamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Universidad.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "No se encontraron universidades con el nombre proporcionado"
                    )
            }
    )
    public ResponseEntity<List<Universidad>> findUniversidadByName(@PathVariable String nombreUniversidad) {
        List<Universidad> ListaUniversidades = universidadService.getUniversidadByName(nombreUniversidad);
        return new ResponseEntity<>(ListaUniversidades, HttpStatus.OK);
    }


    @GetMapping("/getAllComents/{id}")
    @Operation(
            summary = "Obtener número de comentarios de una universidad",
            description = "Este endpoint permite obtener la cantidad total de comentarios asociados a una universidad especificada por su ID.",
            tags = {"Universidades"},
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "ID de la universidad de la cual se desea obtener la cantidad de comentarios.",
                            required = true,
                            in = ParameterIn.PATH,
                            example = "1"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Número de comentarios de la universidad obtenido exitosamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Integer.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Universidad no encontrada"
                    )
            }
    )
    public ResponseEntity<Integer> getAllComents(@PathVariable Long id) {
        Universidad universidad = IuniversidadGenericService.findById(id).get();
        return ResponseEntity.ok(universidad.getListaComentarios().size());
    }


    @GetMapping("/buscarUniversidadesActivas")
    @Operation(
            summary = "Buscar universidades activas",
            description = "Este endpoint permite obtener una lista de IDs de universidades que están marcadas como activas.",
            tags = {"Universidades"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lista de universidades activas obtenida exitosamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Long.class)
                            )
                    )
            }
    )
    public ResponseEntity<List<Long>> buscarUniversidadesActivas() {
        return ResponseEntity.ok(universidadService.buscarUniversidadesActivas());
    }


}
