//package io.hasura
//
//import ndc.ir.*
//import ndc.sqlgen.BaseQueryGenerator
//import ndc.ir.Field as IRField
//import org.jooq.*
//import org.jooq.impl.DSL
//
//
//object CTEQueryGenerator : BaseQueryGenerator() {
//    override fun queryRequestToSQL(
//        request: QueryRequest
//    ): Select<*> {
//        return buildCTEs(request)
//            .select(DSL.jsonArrayAgg(DSL.field(DSL.name(listOf("data", ROWS_AND_AGGREGATES)))))
//            .from(buildSelections(request).asTable("data"))
//    }
//
//    override fun buildComparison(
//        col: Field<Any>,
//        operator: ApplyBinaryComparisonOperator,
//        value: Field<Any>
//    ): Condition {
//        return when (operator) {
//            is ApplyBinaryComparisonOperator.Equal -> col.eq(value)
//            is ApplyBinaryComparisonOperator.Other -> col.like(value as Field<String>)
//            else -> throw Exception("Invalid comparison operator")
//
//        }
//    }
//
//    override fun forEachQueryRequestToSQL(request: QueryRequest): Select<*> {
//        return buildCTEs(request, listOf(buildVarsCTE(request)))
//            .select(DSL.jsonArrayAgg(DSL.field(DSL.name(listOf("data", ROWS_AND_AGGREGATES)))))
//            .from(buildSelections(request).asTable("data"))
//    }
//
//    private fun buildCTEs(request: QueryRequest, varCTE: List<CommonTableExpression<*>> = emptyList()): WithStep {
//        return DSL.with(
//            varCTE +
//                    forEachQueryLevelRecursively(request, CTEQueryGenerator::buildCTE).distinct()
//        )
//    }
//
//    private fun buildCTE(
//        request: QueryRequest,
//        relationship: Relationship?,
//        relSource: String?
//    ): CommonTableExpression<*> {
//
////        return if (request.target is Target.InterpolatedTarget) {
////            mkInterpolatedQueryCTE(request)
////        }
////        else if (request.target is Target.FunctionTarget  && relEdge == null) {
////            mkUdfCTE(request)
////        }
////        else
//        return DSL.name(genCTEName(request.collection)).`as`(
//            DSL.select(DSL.asterisk())
//                .from(
//                    DSL.select(DSL.table(DSL.name(request.collection)).asterisk(),
//                        DSL.rowNumber().over(
//                            DSL.partitionBy(
//                                mkJoinKeyFields(
//                                    relationship, request.collection
//                                )
//                            ).orderBy(
//                                run {
//                                    val orderByFields = translateIROrderByField(request) +
//                                            if (isVariablesRequest(request)) listOf(
//                                                DSL.field(
//                                                    DSL.name(
//                                                        listOf(
//                                                            VARS,
//                                                            "index"
//                                                        )
//                                                    )
//                                                )
//                                            ) else emptyList()
//                                    orderByFields.distinct().ifEmpty { listOf(DSL.trueCondition()) }
//                                }
//                            )
//                        ).`as`(getRNName(request.collection))
//
//                    )
//                        .apply {
//                            if (isVariablesRequest(request))
//                                this.select(DSL.table(DSL.name(VARS)).asterisk())
//                        }
//                        .apply {
//                            if (relationship != null
//                                && (relationship.column_mapping.isNotEmpty() || relationship.arguments.isNotEmpty())
//                            ) {
//                                from(DSL.name(genCTEName(relSource ?: relationship.source_collection_or_type)))
//                                    .innerJoin(DSL.name(relationship.target_collection))
//                                    .on(mkSQLJoin
//                                        (
//                                        relationship,
//                                        sourceTableNameTransform = { genCTEName(relSource ?: it) }
//                                    )
//                                    )
//                            } else from(DSL.name(request.collection))
//                        }
//                        .apply {
//                            addJoinsRequiredForOrderByFields(
//                                this as SelectJoinStep<*>,
//                                request,
//                                sourceTableNameTransform = { request.collection }
//                            )
//                        }
//                        .apply {// cross join "vars" if this request contains variables
//                            if (isVariablesRequest(request))
//                                (this as SelectJoinStep<*>).crossJoin(DSL.name(VARS))
//                        }
//                        .apply {
//                            addJoinsRequiredForPredicate(
//                                request,
//                                this as SelectJoinStep<*>
//                            )
//                        }
//                        .where(getWhereConditions(request))
//                        .asTable(request.collection)
//                ).where(mkOffsetLimit(request, DSL.field(DSL.name(getRNName(request.collection)))))
//        )
//    }
//
////    private fun mkUdfCTE(request: QueryRequest): CommonTableExpression<*> {
////        val target = request.target as Target.FunctionTarget
////        val t = target.getTargetName().value.joinToString(".") { "\"$it\"" }
////        return DSL.name(genCTEName(request.getName())).`as`(
////            DSL.select(DSL.asterisk())
////                .from(
////                    ("TABLE($t(${prepareFunArguments(target.arguments)}))")
////                )
////                .where(getWhereConditions(request))
////                .orderBy(translateIROrderByField(request))
////                .apply {
////                    request.query.limit?.let { limit(it) }
////                    request.query.offset?.let { offset(it) }
////                }
////        )
////    }
//
////    fun mkInterpolatedQueryCTE(request: QueryRequest): CommonTableExpression<*> {
////        if (request.interpolatedQueries == null) throw IllegalArgumentException("No interpolated queries found.")
////        val iq = request.interpolatedQueries!![request.getName().tableName]
////        val queryParts = iq?.items?.map{
////            when(it) {
////                is Either.Left -> it.value
////                is Either.Right -> when(it.value.value_type) {
////                    ScalarType.STRING -> DSL.inline(it.value.value, SQLDataType.VARCHAR)
////                    ScalarType.INT -> DSL.inline(it.value.value, SQLDataType.INTEGER)
////                    ScalarType.FLOAT, ScalarType.NUMBER ->
////                        DSL.inline(it.value.value, SQLDataType.NUMERIC)
////                    ScalarType.BOOLEAN -> DSL.inline(it.value.value, SQLDataType.BOOLEAN)
////                    else -> DSL.inline(it.value.value)
////                }
////            }
////        }
////        return DSL.name(genCTEName(request.getName())).`as`(
////            DSL.resultQuery(queryParts?.joinToString(" "))
////        )
////    }
//
//    private fun <T> forEachQueryLevelRecursively(
//        request: QueryRequest,
//        elementFn: (request: QueryRequest, rel: Relationship?, relSource: String?) -> T
//    ): List<T> {
//
//        fun recur(
//            request: QueryRequest,
//            relationship: Relationship?,
//            relSource: String? = null
//        ): List<T> = buildList {
//            add(elementFn(request, relationship, relSource))
//
//            getQueryRelationFields(request.query.fields).flatMapTo(this) {
//                val rel = request.collection_relationships[it.value.relationship]!!
//                val args =
//                    if (rel.arguments.isEmpty() && rel.column_mapping.isEmpty() && it.value.arguments.isNotEmpty()) {
//                        it.value.arguments
//                    } else rel.arguments
//
//                recur(
//                    request = request.copy(
//                        collection = rel.target_collection,
//                        query = it.value.query
//                    ),
//                    relationship = rel.copy(arguments = args),
//                    request.collection
//                )
//            }
//        }
//
//        return recur(request, null)
//    }
//
//
//    private fun genCTEName(collection: String) = "${collection}_CTE"
//    private fun getRNName(collection: String) = "${collection}_RN"
//
//    private fun buildRows(request: QueryRequest): Field<*> {
//        val isObjectTarget = isTargetOfObjRel(request)
//        val agg = if (isObjectTarget) DSL::jsonArrayAggDistinct else DSL::jsonArrayAgg
//        return DSL.coalesce(
//            agg(buildRow(request))
//                .orderBy(
//                    setOrderBy(request, isObjectTarget)
//                ),
//            DSL.jsonArray()
//        )
//    }
//
//    private fun buildVariableRows(request: QueryRequest): Field<*> {
//        return DSL.arrayAgg(buildRow(request))
//            .over(DSL.partitionBy(DSL.field(DSL.name(listOf(genCTEName(request.collection), "index")))))
//    }
//
//    private fun buildRow(request: QueryRequest): Field<*> {
//        return DSL.jsonObject(
//            (request.query.fields?.map { (alias, field) ->
//                when (field) {
//                    is ndc.ir.Field.ColumnField ->
//                        DSL.jsonEntry(
//                            alias,
//                            DSL.field(DSL.name(genCTEName(request.collection), field.column))
//                        )
//
//                    is ndc.ir.Field.RelationshipField -> {
//                        val relation = request.collection_relationships[field.relationship]!!
//
//                        DSL.jsonEntry(
//                            alias,
//                            DSL.coalesce(
//                                DSL.field(
//                                    DSL.name(
//                                        createAlias(
//                                            relation.target_collection,
//                                            isAggOnlyRelationField(field)
//                                        ),
//                                        ROWS_AND_AGGREGATES
//                                    )
//                                ) as Field<*>,
//                                setRelFieldDefaults(field)
//                            )
//                        )
//                    }
//                }
//            } ?: emptyList<JSONEntry<*>>())
//        )
//    }
//
//    private fun isTargetOfObjRel(request: QueryRequest): Boolean {
//        return request.collection_relationships.values.find {
//            it.target_collection == request.collection && it.relationship_type == RelationshipType.Object
//        } != null
//    }
//
//    private fun setRelFieldDefaults(field: IRField.RelationshipField): Field<*> {
//        return if (isAggOnlyRelationField(field))
//            DSL.jsonObject("aggregates", setAggregateDefaults(field))
//        else if (isAggRelationField(field))
//            DSL.jsonObject(
//                DSL.jsonEntry("rows", DSL.jsonArray()),
//                DSL.jsonEntry("aggregates", setAggregateDefaults(field))
//            )
//        else DSL.jsonObject("rows", DSL.jsonArray())
//    }
//
//    private fun isAggRelationField(field: IRField.RelationshipField) = !field.query.aggregates.isNullOrEmpty()
//
//    private fun isAggOnlyRelationField(field: IRField.RelationshipField) =
//        field.query.fields == null && isAggRelationField(field)
//
//    private fun setAggregateDefaults(field: IRField.RelationshipField): Field<*> =
//        getDefaultAggregateJsonEntries(field.query.aggregates)
//
//    private fun setOrderBy(request: QueryRequest, isObjectTarget: Boolean): List<Field<*>> {
//        return if (isObjectTarget) emptyList()
//        else listOf(DSL.field(DSL.name(getRNName(request.collection))) as Field<*>)
//    }
//
//    private fun buildSelections(request: QueryRequest): Select<*> {
//        val selects = forEachQueryLevelRecursively(request, CTEQueryGenerator::buildSelect)
//
//        // this is a non-relational query so just return the single select
//        if (selects.size == 1) return selects.first().second
//
//
//        selects.forEach { (request, select) ->
//            val relationships = getQueryRelationFields(request.query.fields).values.map {
//                val rel = request.collection_relationships[it.relationship]!!
//                val args = if (rel.arguments.isEmpty() && rel.column_mapping.isEmpty() && it.arguments.isNotEmpty()) {
//                    it.arguments
//                } else rel.arguments
//                rel.copy(arguments = args)
//            }
//
//            relationships.forEach { relationship ->
//
//                val innerSelects = selects.filter { it.first.collection == relationship.target_collection }
//
//                innerSelects.forEach { (innerRequest, innerSelect) ->
//                    val innerAlias = createAlias(
//                        innerRequest.collection, isAggregateOnlyRequest(innerRequest)
//                    )
//
//                    run {
//                        select
//                            .leftJoin(
//                                innerSelect.asTable(innerAlias)
//                            )
//                            .on(
//                                mkSQLJoin(
//                                    relationship,
//                                    sourceTableNameTransform = { genCTEName(request.collection) },
//                                    targetTableNameTransform = { innerAlias }
//                                )
//                            )
//                    }
//                }
//            }
//        }
//        return selects.first().second
//    }
//
//    private fun getTargetFields(rel: Relationship): List<Field<*>>? {
//        return if (rel.column_mapping.isNotEmpty())
//            rel.column_mapping.values.map { it }.map { DSL.field(DSL.name(genCTEName(rel.target_collection), it)) }
//        else if (rel.arguments.isNotEmpty())
//            rel.arguments.keys.map { DSL.field(DSL.name(it)) }
//        else null
//    }
//
//    private fun buildSelect(
//        request: QueryRequest,
//        relationship: Relationship? = null,
//        relSource: String? = null
//    ): Pair<QueryRequest, SelectJoinStep<*>> {
//        val joinFields = if (relationship != null)
//            mkJoinKeyFields(relationship, genCTEName(relationship.target_collection))
//        else emptyList()
//
//        return Pair(
//            request,
//            DSL.selectDistinct(
//                buildOuterStructure(
//                    request,
//                    if (isVariablesRequest(request)) CTEQueryGenerator::buildVariableRows else CTEQueryGenerator::buildRows
//                ).`as`(ROWS_AND_AGGREGATES)
//            )
//                .apply {
//                    this.select(joinFields)
//                }
//                .from(DSL.name(genCTEName(request.collection)))
//                .apply {
//                    if(joinFields.isNotEmpty()) groupBy(joinFields)
//                }
//        )
//    }
//
//    private fun createAlias(collection: String, isAggregateOnly: Boolean): String {
//        return "$collection${if (isAggregateOnly) "_AGG" else ""}"
//    }
//
//}
