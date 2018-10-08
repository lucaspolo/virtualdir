package com.rtjvm.scala.oop.commands
import com.rtjvm.scala.oop.files.{DirEntry, Directory}
import com.rtjvm.scala.oop.filesystem.State

import scala.annotation.tailrec

class Cd(dir: String) extends Command {

  def doFindEntry(root: Directory, path: String): DirEntry = {
    @tailrec
    def findEntryHelper(current: Directory, path: List[String]): DirEntry =
      if(path.isEmpty || path.head.isEmpty) current
      else if (path.tail.isEmpty) current.findEntry(path.head)
      else {
        val nextDir = current.findEntry(path.head)
        if (nextDir == null || !nextDir.isDirectory) null
        else findEntryHelper(nextDir.asDirectory, path.tail)
      }

    @tailrec
    def collapseRelativeTokens(path: List[String], result: List[String]): List[String] = {
      if (path.isEmpty) result
      else if (".".equals(path.head)) collapseRelativeTokens(path.tail, result)
      else if ("..".equals(path.head)) {
        if (result.isEmpty) null
        else collapseRelativeTokens(path.tail, result.init)
      } else collapseRelativeTokens(path.tail, result :+ path.head)
    }

    // 1. Tokens
    val tokens: List[String] = path.substring(1).split(Directory.SEPARATOR).toList

    // 1.5 Eliminate/Collapse relative tokens

    val newTokens = collapseRelativeTokens(tokens, List())

    // 2. Navigate to the correct entry
    if (newTokens == null) null
    else findEntryHelper(root, newTokens)
  }

  override def apply(state: State): State = {
    // find root
    val root = state.root
    val wd = state.wd

    // find the absolute path of the directory I want to cd to
    val absolutePath =
      if (dir.startsWith(Directory.SEPARATOR)) dir
      else if(wd.isRoot) wd.path + dir
      else wd.path + Directory.SEPARATOR + dir

    // find the directory to cd to, given the path
    val destinationDirectory = doFindEntry(root, absolutePath)

    // change the state give the new directory
    if (destinationDirectory == null || !destinationDirectory.isDirectory)
      state.setMessage(dir + ": no such directory!")
    else
      State(root, destinationDirectory.asDirectory)
  }

  
}
