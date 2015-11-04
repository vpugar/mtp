package akka.http.javadsl.marshallers.jackson

import akka.http.impl.server.{MarshallerImpl, UnmarshallerImpl}
import akka.http.javadsl.server.{Marshaller, Unmarshaller}
import akka.http.scaladsl.model.MediaTypes._
import akka.http.scaladsl.{marshalling, unmarshalling}
import com.fasterxml.jackson.databind.{MapperFeature, ObjectMapper}

import scala.reflect.ClassTag

/**
  * JSON errors have checked exceptions that are not handled by ExceptionHandler. By ExceptionHandler are handled only
  * RuntimeException. This implementation throws IllegalArgumentException
  */
object CustomJackson {
  private val objectMapper: ObjectMapper = new ObjectMapper().enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)

  def json[T <: AnyRef]: Marshaller[T] = jsonMarshaller(objectMapper).asInstanceOf[Marshaller[T]]

  def json[T <: AnyRef](objectMapper: ObjectMapper): Marshaller[T] = jsonMarshaller(objectMapper).asInstanceOf[Marshaller[T]]

  def jsonAs[T](clazz: Class[T]): Unmarshaller[T] = jsonAs(objectMapper, clazz)

  def jsonAs[T](objectMapper: ObjectMapper, clazz: Class[T]): Unmarshaller[T] =
    UnmarshallerImpl[T] { (_ec, _materializer) ⇒
      implicit val ec = _ec
      implicit val mat = _materializer

      unmarshalling.Unmarshaller.messageUnmarshallerFromEntityUnmarshaller {
        // isn't implicitly inferred for unknown reasons
        unmarshalling.Unmarshaller.stringUnmarshaller
          .forContentTypes(`application/json`)
          .map { jsonString ⇒
            try {
              val reader = objectMapper.reader(clazz)
              clazz.cast(reader.readValue(jsonString))
            } catch {
              case e: Exception => throw new IllegalArgumentException(e.getMessage, e);
            }
          }
      }
    }(ClassTag(clazz))

  private def jsonMarshaller(objectMapper: ObjectMapper): Marshaller[AnyRef] =
    MarshallerImpl[AnyRef] { implicit ec ⇒
      marshalling.Marshaller.StringMarshaller.wrap(`application/json`) { (value: AnyRef) ⇒
        val writer = objectMapper.writer()
        writer.writeValueAsString(value)
      }
    }
}
