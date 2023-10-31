//package ndc.sqlgen
//
//import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
//import io.hasura.CTEQueryGenerator
//import ndc.ir.QueryRequest
//import org.jooq.SQLDialect
//import org.jooq.conf.Settings
//import org.jooq.conf.StatementType
//import org.jooq.impl.DSL
//import org.junit.jupiter.api.Test
//
//class DataLoaderQueryGeneratorTest {
//    val objectMapper = jacksonObjectMapper()
//
//    val ctx = DSL.using(SQLDialect.SNOWFLAKE, Settings()
//        .withRenderFormatted(true)
//        .withStatementType(StatementType.STATIC_STATEMENT))
//
////    @Test
////    fun simpleQueryIR() {
////        val json = this::class.java.getResource("/SimpleQueryIR.json").readText()
////        val request = objectMapper.readValue(json,QueryRequest::class.java)
////
////        val sql = SimpleQueryGenerator.queryRequestToSQL(request)
////        println("RENDERED:\n\t${ctx.render(sql)}")
////    }
//
//    @Test
//    fun relQueryIR() {
//        val json = this::class.java.getResource("/relOrder.json").readText()
//        val request = objectMapper.readValue(json,QueryRequest::class.java)
//
//        val sql = CTEQueryGenerator.handleRequest(request)
//        println("RENDERED:\n\t${ctx.render(sql)}")
//    }
//
//
//}
//
//
