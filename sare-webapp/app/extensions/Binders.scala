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

import play.api.mvc.{PathBindable, QueryStringBindable}
import java.util.UUID
import edu.sabanciuniv.sentilab.utils.UuidUtils
import play.api.mvc.JavascriptLitteral

object Binders {
  implicit def uuidPathBinder = new PathBindable[UUID] {
    override def bind(key: String, value: String): Either[String, UUID] = {
      if (value == null || UuidUtils.isUuid(value))
        Right(if (value == null) null else UuidUtils.create(value))
      else
        Left(value + " is not a UUID")
    }
    
    override def unbind(key: String, id: UUID): String = {
      UuidUtils.normalize(id.toString())
    }
  }
  
  implicit def uuidJavaScriptLitteral = new JavascriptLitteral[UUID] {
    override def to(id: UUID): String = {
      UuidUtils.normalize(id.toString())
    }
  }
}