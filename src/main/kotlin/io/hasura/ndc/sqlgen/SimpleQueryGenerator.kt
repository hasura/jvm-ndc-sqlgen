//package ndc.sqlgen
//
//import ndc.ir.QueryRequest
//import org.jooq.Field
//import org.jooq.Select
//import org.jooq.Table
//import org.jooq.impl.DSL
//
//object SimpleQueryGenerator : BaseQueryGenerator(){
//    override fun queryRequestToSQL(
//        request: QueryRequest
//    ): Select<*> {
//        return DSL.select(
//            buildOuterStructure(
//                request,
//                this::buildRows
//            )
//        )
//        .from(buildSimpleFrom(request))
//    }
//
//    private fun buildSimpleFrom(request: QueryRequest): Table<*> {
//        return DSL.select(
//                DSL.asterisk()
//            )
//            .from(
//                DSL.table(DSL.name(request.collection)
//            ))
//            .where(getWhereConditions(request))
//            .orderBy(translateIROrderByField(request))
//            .apply {
//                request.query.limit?.let { limit(it) }
//                request.query.offset?.let { offset(it) }
//            }.asTable(request.collection)
//    }
//
//    private fun buildRows(request: QueryRequest): Field<*> {
//        return DSL.coalesce(
//            DSL.jsonArrayAgg(
//                DSL.jsonObject(
//                    request.query.fields?.map { (alias, field) ->
//                        DSL.jsonEntry(
//                            alias,
//                            DSL.field(DSL.name(request.collection, (field as ndc.ir.Field.ColumnField).column))
//                        )
//                    }
//                )
//            )
//                .apply{
//                    request.query.order_by?.let{
//                        orderBy(
//                            translateIROrderByField(request)
//                        )
//                    }
//                },
//            DSL.jsonArray()
//        )
//    }
//
//    override fun forEachQueryRequestToSQL(request: QueryRequest): Select<*> = DSL.select()
//        //DataLoaderQueryGenerator.forEachQueryRequestToSQL(request)
//}
