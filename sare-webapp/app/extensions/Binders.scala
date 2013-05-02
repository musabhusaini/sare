/*
 * Sentilab SARE: a Sentiment Analysis Research Environment
 * Copyright (C) 2013 Sabanci University Sentilab
 * http://sentilab.sabanciuniv.edu
 * 
 * This file is part of SARE.
 * 
 * SARE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * SARE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SARE. If not, see <http://www.gnu.org/licenses/>.
 */

package extensions

import java.util.UUID

import play.api.mvc.{PathBindable, QueryStringBindable, JavascriptLitteral}

import edu.sabanciuniv.sentilab.utils.UuidUtils

object Binders {
  class UuidBinder {
    def parse(value: String): Either[String, UUID] = {
      value match {
        case null => Right(null)
        case _ => {
          if (UuidUtils.isUuid(value))
            Right(UuidUtils.create(value))
          else
            Left(s"$value is not a valid UUID")
        }
      }
    }
    
    def serialize(id: UUID): String = {
      id match {
        case null => null
        case _ => UuidUtils.normalize(id)
      }
    }
  }
  
  implicit object javascriptLitteralUuid extends UuidBinder with JavascriptLitteral[UUID] {
    override def to(id: UUID): String = serialize(id)
  }
  
  implicit object pathBindableUuid extends UuidBinder with PathBindable[UUID] {
    override def bind(key: String, value: String): Either[String, UUID] = parse(value)
    
    override def unbind(key: String, id: UUID): String = serialize(id)
  }
  
  implicit object queryStringBindableUuid extends UuidBinder with QueryStringBindable[UUID] {
    override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, UUID]] = {
      params.get(key).flatMap(_.headOption).map { p => parse(p) }
    }
    
    override def unbind(key: String, id: UUID): String = {
      serialize(id) match {
        case null => null
        case value => s"$key=$value"
      }
    }
  }
}