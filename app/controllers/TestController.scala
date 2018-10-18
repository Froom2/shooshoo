package controllers

import java.time.LocalDateTime
import javax.inject.Inject

import org.mongodb.scala.bson.collection.Document
import org.mongodb.scala.{Completed, MongoClient, MongoCollection, MongoDatabase, Observable, Observer}
import play.api.Configuration
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents, Request}

class TestController @Inject() (
                               components: ControllerComponents,
                               config: Configuration
                             ) extends AbstractController(components) with App {




  def test() = Action { implicit request: Request[AnyContent] =>

    val databaseUri = config.get[String]("mongodb.uri")
    val mongoClient: MongoClient = MongoClient(databaseUri)
    val database: MongoDatabase = mongoClient.getDatabase("mydb")
    val collection: MongoCollection[Document] = database.getCollection("test")
    val doc: Document = Document("_id" -> LocalDateTime.now.toString, "name" -> "MongoDB", "type" -> "database",
      "count" -> 1, "info" -> Document("x" -> 203, "y" -> 102))

    println("database: " + databaseUri)

    val observable: Observable[Completed] = collection.insertOne(doc)

    observable.subscribe(new Observer[Completed] {

      override def onNext(result: Completed): Unit = println("Inserted")

      override def onError(e: Throwable): Unit = println("Failed")

      override def onComplete(): Unit = println("Completed")
    })

    Ok(views.html.test())
  }
}