/* sbt -- Simple Build Tool
 * Copyright 2008, 2009 Mark Harrah
 */
package xsbt

import xsbti.{ AnalysisCallback, Logger, Problem, Reporter, Severity }
import xsbti.compile._
import scala.tools.nsc.{ backend, io, reporters, symtab, util, Phase, Global, Settings, SubComponent }
import scala.tools.nsc.interactive.RangePositions
import backend.JavaPlatform
import scala.tools.util.PathResolver
import symtab.SymbolLoaders
import util.{ ClassPath, DirectoryClassPath, MergedClassPath, JavaClassPath }
import ClassPath.{ ClassPathContext, JavaContext }
import io.AbstractFile
import scala.annotation.tailrec
import scala.collection.mutable
import Log.debug
import java.io.File

import dotty.tools.dotc.{ Main => DottyMain }

final class CompilerInterface {
  def newCompiler(options: Array[String], output: Output, initialLog: Logger, initialDelegate: Reporter, resident: Boolean): CachedCompiler =
    new CachedCompiler0(options, output, new WeakLog(initialLog, initialDelegate), resident)

  def run(sources: Array[File], changes: DependencyChanges, callback: AnalysisCallback, log: Logger, delegate: Reporter, progress: CompileProgress, cached: CachedCompiler): Unit =
    cached.run(sources, changes, callback, log, delegate, progress)
}

private final class WeakLog(private[this] var log: Logger, private[this] var delegate: Reporter) {
  def apply(message: String): Unit = {
    assert(log ne null, "Stale reference to logger")
    log.error(Message(message))
  }
  def logger: Logger = log
  def reporter: Reporter = delegate
  def clear(): Unit = {
    log = null
    delegate = null
  }
}

private final class CachedCompiler0(args: Array[String], output: Output, initialLog: WeakLog, resident: Boolean) extends CachedCompiler {
  val outputArgs =
    output match {
      case multi: MultipleOutput =>
        ???
      case single: SingleOutput =>
        List("-d", single.outputDirectory.getAbsolutePath.toString)
    }

  def commandArguments(sources: Array[File]): Array[String] =
    (outputArgs ++ args.toList ++ sources.map(_.getAbsolutePath).sortWith(_ < _)).toArray[String]

  def run(sources: Array[File], changes: DependencyChanges, callback: AnalysisCallback, log: Logger, delegate: Reporter, progress: CompileProgress): Unit = synchronized {
    run(sources.toList, changes, callback, log, progress)
  }
  private[this] def run(sources: List[File], changes: DependencyChanges, callback: AnalysisCallback, log: Logger, compileProgress: CompileProgress): Unit = {
    debug(log, args.mkString("Calling Dotty compiler with arguments  (CompilerInterface):\n\t", "\n\t", ""))
    val reporter = DottyMain.process(commandArguments(sources.toArray))
    ()
  }
}
