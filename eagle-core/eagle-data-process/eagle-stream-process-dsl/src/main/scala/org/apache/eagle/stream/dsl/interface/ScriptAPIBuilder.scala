package org.apache.eagle.stream.dsl.interface

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
class ScriptString(content:String,scriptType:String) extends Serializable{
  def getContent:String = content
  def getScriptType:String = scriptType
}

case class SqlScript(content:String) extends ScriptString(content,"sql")
trait ScriptAPIBuilder extends AbstractAPIBuilder{
  implicit class ScriptStringImplicits(val sc:StringContext) extends AnyVal{
    def sql(arg:Any):SqlScript = SqlScript(arg.asInstanceOf[String])
  }
}