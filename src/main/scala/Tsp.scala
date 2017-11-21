sealed trait TspType
case object TSPTspType extends TspType

sealed trait EdgeWeightType
case object EUC_2D extends EdgeWeightType

case class CityName(value: String) extends AnyVal

case class Tsp(name: String, ty: TspType, dimension: Int, edgeWeightType: EdgeWeightType, nodeCoordSection: Map[CityName, (Double, Double)])
