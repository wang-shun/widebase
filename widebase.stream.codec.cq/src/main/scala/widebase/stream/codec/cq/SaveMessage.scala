package widebase.stream.codec.cq

import widebase.db.table.Table

/** Save table into cache.
 *
 * @param name of table
 * @param table self-explanatory
 *
 * @author myst3r10n
 */
class SaveMessage(val name: String, val table: Table)

