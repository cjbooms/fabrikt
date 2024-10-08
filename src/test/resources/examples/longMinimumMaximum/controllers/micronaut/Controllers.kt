package examples.longMinimumMaximum.controllers

import io.micronaut.http.HttpResponse
import io.micronaut.http.`annotation`.Controller
import io.micronaut.http.`annotation`.Get
import io.micronaut.http.`annotation`.Header
import io.micronaut.http.`annotation`.PathVariable
import io.micronaut.http.`annotation`.QueryValue
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import kotlin.Long
import kotlin.Unit

@Controller
public interface MaximumTestController {
    /**
     *
     *
     * @param pathId
     * @param headerid
     * @param queryid
     */
    @Get(uri = "/maximumTest/{pathId}")
    public fun getById(
        @Min(0) @Max(4_294_967_295) @PathVariable(value = "pathId") pathId: Long?,
        @Min(0) @Max(4_294_967_295) @Header(value = "headerid") headerid: Long?,
        @Min(0) @Max(4_294_967_295) @QueryValue(value = "queryid") queryid: Long?,
    ): HttpResponse<Unit>
}

@Controller
public interface MinimumTestController {
    /**
     *
     *
     * @param pathId
     * @param headerid
     * @param queryid
     */
    @Get(uri = "/minimumTest/{pathId}")
    public fun getById(
        @Min(-4_294_967_295) @PathVariable(value = "pathId") pathId: Long?,
        @Min(-4_294_967_295) @Header(value = "headerid") headerid: Long?,
        @Min(-4_294_967_295) @QueryValue(value = "queryid") queryid: Long?,
    ): HttpResponse<Unit>
}

@Controller
public interface MinMaxTestController {
    /**
     *
     *
     * @param pathId
     * @param headerid
     * @param queryid
     */
    @Get(uri = "/minMaxTest/{pathId}")
    public fun getById(
        @Min(-4_294_967_295) @Max(4_294_967_296) @PathVariable(value = "pathId") pathId: Long?,
        @Min(-4_294_967_295) @Max(4_294_967_296) @Header(value = "headerid") headerid: Long?,
        @Min(-4_294_967_295) @Max(4_294_967_296) @QueryValue(value = "queryid") queryid: Long?,
    ): HttpResponse<Unit>
}
