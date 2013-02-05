package widebase.stream.handler.rq

import java.io.FileDescriptor
import java.net.InetAddress
import java.security.Permission

/** Security policy of selector parser and executor.
 *
 * @author myst3r10n
 */
class SelectorPolicy extends SecurityManager {
/* Must be commented
  override def checkAccept(host: String, port: Int) {

    throw new SecurityException

  }

  override def checkAccess(t: Thread) {

    throw new SecurityException

  }

  override def checkAccess(g: ThreadGroup) {

    throw new SecurityException

  }
*/
  override def checkAwtEventQueueAccess {

    throw new SecurityException

  }

  override def checkConnect(host: String, port: Int) {

    throw new SecurityException

  }

  override def checkConnect(host: String, port: Int, context: Object) {

    throw new SecurityException

  }
/* Must be commented
  override def checkCreateClassLoader {

    throw new SecurityException

  }
*/
  override def checkDelete(file: String) {

    throw new SecurityException

  }

  override def checkExec(cmd: String) {

    throw new SecurityException

  }

  override def checkExit(status: Int) {

    throw new SecurityException

  }
/* Must be commented
  override def checkLink(lib: String) {

    throw new SecurityException

  }
*/
  override def checkListen(port: Int) {

    throw new SecurityException

  }
/* Must be commented
  override def checkMemberAccess(clazz: Class[_], which: Int) {

    throw new SecurityException

  }
*/
  override def checkMulticast(maddr: InetAddress) {

    throw new SecurityException

  }
/* Must be commented
  override def checkPackageAccess(pkg: String) {

    throw new SecurityException

  }
*/
  override def checkPackageDefinition(pkg: String) {

    throw new SecurityException

  }
/* Must be commented
  override def checkPermission(perm: Permission) {

    throw new SecurityException

  }
*/
  override def checkPermission(perm: Permission, context: Object) {

    throw new SecurityException

  }

  override def checkPrintJobAccess {

    throw new SecurityException

  }
/* Must be commented
  override def checkPropertiesAccess {

    throw new SecurityException

  }

  override def checkPropertyAccess(key: String) {

    throw new SecurityException

  }
*/
  override def checkRead(fd: FileDescriptor) {

    throw new SecurityException

  }
/* Must be commented
  override def checkRead(file: String) {

    throw new SecurityException

  }
*/
  override def checkRead(file: String, context: Object) {

    throw new SecurityException

  }
/* Must be commented
  override def checkSecurityAccess(target: String) {

    throw new SecurityException

  }
*/
  override def checkSetFactory {

    throw new SecurityException

  }

  override def checkSystemClipboardAccess {

    throw new SecurityException

  }

  override def checkTopLevelWindow(window: Object): Boolean = {

    throw new SecurityException

  }

  override def checkWrite(fd: java.io.FileDescriptor) {

    throw new SecurityException

  }

  override def checkWrite(file: String) {

    throw new SecurityException

  }
}

