//@file:Suppress("UnstableApiUsage")
//
//package gdc.sqlgen.utils
//
//import com.google.common.graph.MutableNetwork
//import com.google.common.graph.NetworkBuilder
//import ndc.ir.MutationRequest
//import ndc.ir.QueryRequest
//import ndc.ir.Relationship
//
//
//data class RelationshipEdge(
//    val collection: String,
//    val relationshipName: String,
//    val entry: Relationship
//)
//
//// Represents the relationship graph of a single operation request (Query/Mutation)
//interface IOperationRequestRelationGraph {
//    fun getOutgoingRelationsForTable(collection: String): Set<RelationshipEdge>
//    fun getRelation(currentCollection: String, relationship: String): RelationshipEdge
//    fun traverseRelEdges(path: List<TableNamePart>, startingTable: FullyQualifiedTableName): List<RelationshipEdge>
//}
//
//class OperationRequestRelationGraph(val tableRelationships: Map<String, Relationship>) :
//    IOperationRequestRelationGraph {
//
//    constructor(queryRequest: QueryRequest) : this(queryRequest.collection_relationships)
//    constructor(mutationRequest: MutationRequest) : this(mutationRequest.collection_relationships)
//
//    private val graph: MutableNetwork<String, RelationshipEdge> =
//        NetworkBuilder.directed()
//            .allowsSelfLoops(true)
//            .allowsParallelEdges(true)
//            .build()
//
//    init {
//        tableRelationships.forEach {
//                graph.addEdge(
//                    it.value.source_collection_or_type,
//                    it.value.target_collection,
//                    RelationshipEdge(it.value.source_collection_or_type, it.key, it.value)
//                )
//        }
//    }
//
//    override fun getOutgoingRelationsForTable(collection: String): Set<RelationshipEdge> = try {
//        graph.outEdges(collection)
//    } catch (e: IllegalArgumentException) {
//        emptySet()
//    }
//
//    override fun getRelation(
//        currentCollection: String,
//        relationship: String
//    ): RelationshipEdge {
//        val t = getOutgoingRelationsForTable(currentCollection)
//        return t.first { it.relationshipName == relationship }
//    }
//
//    override fun traverseRelEdges(
//        path: List<TableNamePart>,
//        startingTable: FullyQualifiedTableName
//    ): List<RelationshipEdge> {
//        val edges = mutableListOf<RelationshipEdge>()
//        var currentTable = startingTable
//        path.forEach { tableNamePart ->
//            val edge = getRelation(currentTable, RelationshipName(tableNamePart.value))
//            edges.add(edge)
//            currentTable = edge.entry.target.getTargetName()
//        }
//        return edges
//    }
//}
