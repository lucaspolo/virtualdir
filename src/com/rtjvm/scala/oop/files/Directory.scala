package com.rtjvm.scala.oop.files

class Directory(override val parentPath: String, override val name: String, val contents: List[DirEntry])
  extends DirEntry(parentPath, name) {
  def replaceEntry(entryName: String, newEntry: Directory): Directory = ???

  def findEntry(entryName: String): DirEntry = ???

  def addEntry(newEntry: DirEntry): Directory = ???

  def hasEntry(name: String): Boolean = ???

  def getAllFoldersInPath: List[String] =
    path.substring(1).split(Directory.SEPARATOR).toList

  def findDescendant(path: List[String]): Directory = ???

  override def asDirectory: Directory = this

}

object Directory {
  val SEPARATOR = "/"
  val ROOT_PATH = "/"

  def ROOT: Directory = Directory.empty("", "");

  def empty(parentPath: String, name: String) =
    new Directory(parentPath, name, List())
}