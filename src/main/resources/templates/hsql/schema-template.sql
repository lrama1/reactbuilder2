#set($domainVar = $domainClassName.substring(0,1).toLowerCase() + $domainClassName.substring(1))
CREATE TABLE ${domainVar} (
    #foreach($attribute in $attributes )
    #set($columnName = "")
    #set($datatype = "")
	#if(${persistenceType} == 'HSQL')
      #set($columnName = $oracleNames.get(${attribute.name}))
    #else
      #set($columnName = ${attribute.name})
    #end

    #if(${attribute.dataType} == 'String')
      #set($datatype = "VARCHAR(64)")
    #elseif(${attribute.dataType} == 'Date')
      #set($datatype = "DATE")
    #elseif(${attribute.dataType} == 'Integer')
      #set($datatype = "NUMERIC")
    #end

    #if(${attribute.name} == ${domainClassIdAttributeName})
      $columnName $datatype PRIMARY KEY
    #else
      ,$columnName $datatype
    #end
  #end
);
