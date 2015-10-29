/* sbt -- Simple Build Tool
 * Copyright 2008, 2009 Mark Harrah
 */
package xsbt

import xsbti.Logger
import scala.tools.nsc.{ GenericRunnerCommand, Interpreter, ObjectRunner, Settings }
import scala.tools.nsc.interpreter.{ IMain, InteractiveReader, ILoop }
import scala.tools.nsc.reporters.Reporter
import scala.tools.nsc.util.ClassPath

class ConsoleInterface {
  def commandArguments(args: Array[String], bootClasspathString: String, classpathString: String, log: Logger): Array[String] = ???

  def run(args: Array[String], bootClasspathString: String, classpathString: String, initialCommands: String, cleanupCommands: String, loader: ClassLoader, bindNames: Array[String], bindValues: Array[Any], log: Logger): Unit = ???
}
