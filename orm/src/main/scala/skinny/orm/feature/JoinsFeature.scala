package skinny.orm.feature

import skinny.orm._
import skinny.orm.feature.associations._
import skinny.orm.feature.includes.IncludesQueryRepository
import scalikejdbc._, SQLInterpolation._

/**
 * Provides #joins APIs.
 *
 * NOTE: CRUDFeature has copy implementation from this trait.
 */
trait JoinsFeature[Entity] extends SkinnyMapperBase[Entity] with AssociationsFeature[Entity] { self: IdFeature[_] =>

  /**
   * Appends join definition on runtime.
   *
   * @param associations associations
   * @return self
   */
  def joins[Id](associations: Association[_]*): JoinsFeature[Entity] with IdFeature[Id] with FinderFeatureWithId[Id, Entity] with QueryingFeatureWithId[Id, Entity] = {
    val _self = this
    val _associations = associations

    new JoinsFeature[Entity] with IdFeature[Id] with FinderFeatureWithId[Id, Entity] with QueryingFeatureWithId[Id, Entity] {
      override protected val underlying = _self
      override def defaultAlias = _self.defaultAlias

      override def rawValueToId(value: Any) = _self.rawValueToId(value).asInstanceOf[Id]
      override def idToRawValue(id: Id) = id

      override def associations = _self.associations ++ _associations

      override val defaultJoinDefinitions = _self.defaultJoinDefinitions
      override val defaultBelongsToExtractors = _self.defaultBelongsToExtractors
      override val defaultHasOneExtractors = _self.defaultHasOneExtractors
      override val defaultOneToManyExtractors = _self.defaultOneToManyExtractors

      override def autoSession = underlying.autoSession
      override def connectionPoolName = underlying.connectionPoolName
      override def connectionPool = underlying.connectionPool

      override def defaultScope(alias: Alias[Entity]): Option[SQLSyntax] = _self.defaultScope(alias)

      def extract(rs: WrappedResultSet, n: SQLInterpolation.ResultName[Entity]) = underlying.extract(rs, n)
    }
  }

  override def extract(sql: SQL[Entity, NoExtractor])(
    implicit includesRepository: IncludesQueryRepository[Entity]): SQL[Entity, HasExtractor] = {
    extractWithAssociations(
      sql,
      belongsToAssociations,
      hasOneAssociations,
      hasManyAssociations)
  }

}
