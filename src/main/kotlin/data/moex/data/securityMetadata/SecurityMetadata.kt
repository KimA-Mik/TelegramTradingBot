package data.moex.data.securityMetadata

data class SecurityMetadata(
    val description: List<DescriptionEntry>,
    val boards: List<BoardEntry>
)
//"name": "SECID", "title": "Код ценной бумаги"
//"name": "NAME", "title": "Полное наименование"
//"name": "SHORTNAME", "title": "Краткое наименование"
//"name": "ISIN", "title": "ISIN код"
//"name": "REGNUMBER", "title": "Номер государственной регистрации"
//"name": "ISSUESIZE", "title": "Объем выпуска"
//"name": "FACEVALUE", "title": "Номинальная стоимость"
//"name": "FACEUNIT", "title": "Валюта номинала"
//"name": "ISSUEDATE", "title": "Дата начала торгов"
//"name": "LATNAME", "title": "Английское наименование"
//"name": "LISTLEVEL", "title": "Уровень листинга"
//"name": "ISQUALIFIEDINVESTORS", "title": "Бумаги для квалифицированных инвесторов"
//"name": "MORNINGSESSION", "title": "Допуск к утренней дополнительной торговой сессии"
//"name": "EVENINGSESSION", "title": "Допуск к вечерней дополнительной торговой сессии"
//"name": "TYPENAME", "title": "Вид\/категория ценной бумаги"
//"name": "GROUP", "title": "Код типа инструмента"
//"name": "TYPE", "title": "Тип бумаги"
//"name": "GROUPNAME", "title": "Типа инструмента"
//"name": "EMITTER_ID", "title": "Код эмитента"

//fun SecurityMetadata.toSecurity(): Security {
//    return Security(
//
//    )
//}