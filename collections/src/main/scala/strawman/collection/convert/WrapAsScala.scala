/*                     __                                               *\
**     ________ ___   / /  ___     Scala API                            **
**    / __/ __// _ | / /  / _ |    (c) 2006-2013, LAMP/EPFL             **
**  __\ \/ /__/ __ |/ /__/ __ |    http://www.scala-lang.org/           **
** /____/\___/_/ |_/____/_/ | |                                         **
**                          |/                                          **
\*                                                                      */

package strawman
package collection
package convert

import scala.deprecated
import scala.Predef.String
import java.{ lang => jl, util => ju }, java.util.{ concurrent => juc }
import scala.language.implicitConversions
import scala.Null

@deprecated("use JavaConverters or consider ToScalaImplicits", since="2.12.0")
trait WrapAsScala extends LowPriorityWrapAsScala {
  // provide higher-priority implicits with names that don't exist in JavaConverters for the case
  // when importing both JavaConverters._ and JavaConversions._. otherwise implicit conversions
  // would not apply, see https://github.com/scala/scala/pull/5109#issuecomment-212417789
  implicit def `deprecated asScalaIterator`[A](it: ju.Iterator[A]): Iterator[A | Null] = asScalaIterator(it)
  implicit def `deprecated enumerationAsScalaIterator`[A](i: ju.Enumeration[A]): Iterator[A | Null] = enumerationAsScalaIterator(i)
  implicit def `deprecated iterableAsScalaIterable`[A](i: jl.Iterable[A]): Iterable[A | Null] = iterableAsScalaIterable(i)
  implicit def `deprecated collectionAsScalaIterable`[A](i: ju.Collection[A]): Iterable[A | Null] = collectionAsScalaIterable(i)
  implicit def `deprecated asScalaBuffer`[A](l: ju.List[A]): mutable.Buffer[A | Null] = asScalaBuffer(l)
  implicit def `deprecated asScalaSet`[A](s: ju.Set[A]): mutable.Set[A | Null] = asScalaSet(s)
  implicit def `deprecated mapAsScalaMap`[A, B](m: ju.Map[A, B]): mutable.Map[A, B | Null] = mapAsScalaMap(m)
  implicit def `deprecated mapAsScalaConcurrentMap`[A, B](m: juc.ConcurrentMap[A, B]): concurrent.Map[A, B | Null] = mapAsScalaConcurrentMap(m)
  implicit def `deprecated dictionaryAsScalaMap`[A, B](p: ju.Dictionary[A, B]): mutable.Map[A, B | Null] = dictionaryAsScalaMap(p)
  implicit def `deprecated propertiesAsScalaMap`(p: ju.Properties): mutable.Map[String, String | Null] = propertiesAsScalaMap(p)
}

private[convert] trait LowPriorityWrapAsScala {
  import Wrappers._

  /**
   * Implicitly converts a Java `Iterator` to a Scala `Iterator`.
   *
   * The returned Scala `Iterator` is backed by the provided Java `Iterator`
   * and any side-effects of using it via the Scala interface will be visible
   * via the Java interface and vice versa.
   *
   * If the Java `Iterator` was previously obtained from an implicit or
   * explicit call of `asIterator(scala.collection.Iterator)` then the
   * original Scala `Iterator` will be returned.
   *
   * @param it The `Iterator` to be converted.
   * @return   A Scala `Iterator` view of the argument.
   */
  implicit def asScalaIterator[A](it: ju.Iterator[A]): Iterator[A | Null] = it match {
    case IteratorWrapper(wrapped) => wrapped
    case _                        => JIteratorWrapper(it)
  }

  /**
   * Implicitly converts a Java Enumeration to a Scala Iterator.
   * The returned Scala Iterator is backed by the provided Java
   * Enumeration and any side-effects of using it via the Scala interface will
   * be visible via the Java interface and vice versa.
   *
   * If the Java Enumeration was previously obtained from an implicit or
   * explicit call of `enumerationAsScalaIterator(scala.collection.Iterator)`
   * then the original Scala Iterator will be returned.
   *
   * @param i The Enumeration to be converted.
   * @return A Scala Iterator view of the argument.
   */
  implicit def enumerationAsScalaIterator[A](i: ju.Enumeration[A]): Iterator[A | Null] = i match {
    case IteratorWrapper(wrapped) => wrapped
    case _                        => JEnumerationWrapper(i)
  }

  /**
   * Implicitly converts a Java `Iterable` to a Scala `Iterable`.
   *
   * The returned Scala `Iterable` is backed by the provided Java `Iterable`
   * and any side-effects of using it via the Scala interface will be visible
   * via the Java interface and vice versa.
   *
   * If the Java `Iterable` was previously obtained from an implicit or
   * explicit call of `iterableAsScalaIterable(scala.collection.Iterable)`
   * then the original Scala Iterable will be returned.
   *
   * @param i The Iterable to be converted.
   * @return A Scala Iterable view of the argument.
   */
  implicit def iterableAsScalaIterable[A](i: jl.Iterable[A]): Iterable[A | Null] = i match {
    case IterableWrapper(wrapped) => wrapped
    case _                        => JIterableWrapper(i)
  }

  /**
   * Implicitly converts a Java `Collection` to an Scala `Iterable`.
   *
   * If the Java `Collection` was previously obtained from an implicit or
   * explicit call of `collectionAsScalaIterable(scala.collection.SizedIterable)`
   * then the original Scala `Iterable` will be returned.
   *
   * @param i The Collection to be converted.
   * @return A Scala Iterable view of the argument.
   */
  implicit def collectionAsScalaIterable[A](i: ju.Collection[A]): Iterable[A | Null] = i match {
    case IterableWrapper(wrapped) => wrapped
    case _                        => JCollectionWrapper(i)
  }

  /**
   * Implicitly converts a Java `List` to a Scala mutable `Buffer`.
   *
   * The returned Scala `Buffer` is backed by the provided Java `List`
   * and any side-effects of using it via the Scala interface will
   * be visible via the Java interface and vice versa.
   *
   * If the Java `List` was previously obtained from an implicit or
   * explicit call of `asScalaBuffer(scala.collection.mutable.Buffer)`
   * then the original Scala `Buffer` will be returned.
   *
   * @param l The `List` to be converted.
   * @return A Scala mutable `Buffer` view of the argument.
   */
  implicit def asScalaBuffer[A](l: ju.List[A]): mutable.Buffer[A | Null] = l match {
    case MutableBufferWrapper(wrapped)  => wrapped.asInstanceOf[mutable.Buffer[A | Null]]
    case _                              => new JListWrapper(l)
  }

  /**
   * Implicitly converts a Java Set to a Scala mutable Set.
   * The returned Scala Set is backed by the provided Java
   * Set and any side-effects of using it via the Scala interface will
   * be visible via the Java interface and vice versa.
   *
   * If the Java Set was previously obtained from an implicit or
   * explicit call of `asScalaSet(scala.collection.mutable.Set)` then
   * the original Scala Set will be returned.
   *
   * @param s The Set to be converted.
   * @return A Scala mutable Set view of the argument.
   */
  implicit def asScalaSet[A](s: ju.Set[A]): mutable.Set[A | Null] = s match {
    case MutableSetWrapper(wrapped) => wrapped.asInstanceOf[mutable.Set[A | Null]]
    case _                          => new JSetWrapper(s.asInstanceOf[ju.Set[A | Null]])
  }

  /**
   * Implicitly converts a Java `Map` to a Scala mutable `Map`.
   *
   * The returned Scala `Map` is backed by the provided Java `Map` and any
   * side-effects of using it via the Scala interface will be visible via
   * the Java interface and vice versa.
   *
   * If the Java `Map` was previously obtained from an implicit or
   * explicit call of `mapAsScalaMap(scala.collection.mutable.Map)` then
   * the original Scala Map will be returned.
   *
   * If the wrapped map is synchronized (e.g. from `java.util.Collections.synchronizedMap`),
   * it is your responsibility to wrap all
   * non-atomic operations with `underlying.synchronized`.
   * This includes `get`, as `java.util.Map`'s API does not allow for an
   * atomic `get` when `null` values may be present.
   *
   * @param m The Map to be converted.
   * @return A Scala mutable Map view of the argument.
   */
  implicit def mapAsScalaMap[A, B](m: ju.Map[A, B]): mutable.Map[A, B | Null] = m match {
    case MutableMapWrapper(wrapped) => wrapped.asInstanceOf[mutable.Map[A, B | Null]]
    case _                          => new JMapWrapper(m)
  }

  /**
   * Implicitly converts a Java ConcurrentMap to a Scala mutable ConcurrentMap.
   * The returned Scala ConcurrentMap is backed by the provided Java
   * ConcurrentMap and any side-effects of using it via the Scala interface will
   * be visible via the Java interface and vice versa.
   *
   * If the Java ConcurrentMap was previously obtained from an implicit or
   * explicit call of `asConcurrentMap(scala.collection.mutable.ConcurrentMap)`
   * then the original Scala ConcurrentMap will be returned.
   *
   * @param m The ConcurrentMap to be converted.
   * @return A Scala mutable ConcurrentMap view of the argument.
   */
  implicit def mapAsScalaConcurrentMap[A, B](m: juc.ConcurrentMap[A, B]): concurrent.Map[A, B | Null] = m match {
    case cmw: ConcurrentMapWrapper[_, _]  => cmw.underlying.asInstanceOf[concurrent.Map[A, B | Null]]
    case _                                => new JConcurrentMapWrapper(m.asInstanceOf[juc.ConcurrentMap[A, B | Null]])
  }

  /**
   * Implicitly converts a Java `Dictionary` to a Scala mutable
   * `Map`.
   *
   * The returned Scala `Map` is backed by the provided Java
   * `Dictionary` and any side-effects of using it via the Scala interface
   * will be visible via the Java interface and vice versa.
   *
   * @param p The Dictionary to be converted.
   * @return  A Scala mutable Map view of the argument.
   */
  implicit def dictionaryAsScalaMap[A, B](p: ju.Dictionary[A, B]): mutable.Map[A, B | Null] = p match {
    case DictionaryWrapper(wrapped) => wrapped.asInstanceOf[mutable.Map[A, B | Null]]
    case _                          => new JDictionaryWrapper(p.asInstanceOf[ju.Dictionary[A, B | Null]])
  }

  /**
   * Implicitly converts a Java `Properties` to a Scala `mutable Map[String, String]`.
   *
   * The returned Scala `Map[String, String]` is backed by the provided Java
   * `Properties` and any side-effects of using it via the Scala interface
   * will be visible via the Java interface and vice versa.
   *
   * @param p The Properties to be converted.
   * @return  A Scala mutable Map[String, String] view of the argument.
   */
  implicit def propertiesAsScalaMap(p: ju.Properties): mutable.Map[String, String | Null] = p match {
    case _    => new JPropertiesWrapper(p)
  }
}

@deprecated("use JavaConverters or consider ImplicitConversionsToScala", since="2.12.0")
object WrapAsScala extends WrapAsScala
