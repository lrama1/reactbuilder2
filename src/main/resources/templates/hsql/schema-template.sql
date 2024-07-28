#set($domainVar = $domainClassName.substring(0,1).toLowerCase() + $domainClassName.substring(1))
CREATE TABLE ${domainVar} (
    #foreach($key in $attributes.keySet() )
    #set($columnName = "")
    #set($datatype = "")
    #if(${persistenceType} == 'HSQL')
      #set($columnName = $oracleNames.get(${key}))
      #else
      #set($columnName = $key)
      #end
      #if($attributes.get(${key}) == 'String')
      #set($datatype = "VARCHAR(64)")
      #elseif($attributes.get(${key}) == 'Date')
      #set($datatype = "DATE")
      #elseif($attributes.get(${key}) == 'Integer')
      #set($datatype = "NUMERIC")
      #end
      #if($key == ${domainClassIdAttributeName})
      $columnName $datatype PRIMARY KEY
    #else
    ,$columnName $datatype
    #end
  #end
);
