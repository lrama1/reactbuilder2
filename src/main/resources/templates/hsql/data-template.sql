#set($domainVar = $domainClassName.substring(0,1).toLowerCase() + $domainClassName.substring(1))
#set($start = 0)
#set($end = 13)
#set($range = [$start..$end])
#set($newline = '\n')
INSERT INTO ${domainVar}
(
  #foreach($attribute in $attributes )
    #set($columnName = "")
    #if(${persistenceType} == 'HSQL')
      #set($columnName = $oracleNames.get(${attribute.name}))
    #else
      #set($columnName = ${attribute.name})
    #end

    #if($foreach.index == 0)
      $columnName
    #else
      ,$columnName
    #end
#end
)

VALUES
  #foreach($i in $range)
    #if($i >= 1)
     ,
    #end##

    #set($line = '')
    #set($index = 0)
    #foreach($attribute in $attributes )
      #if($index == 0)
        #if(${attribute.dataType} == 'String')
          #set($line = $line + "'Sample-" + ${attribute.name} + ${i} + "'")
        #elseif(${attribute.dataType} == 'Date')
          #set($line = $line + "'2018-08-21'")
        #elseif(${attribute.dataType} == 'Number')
          #set($line = $line + "'1000'")
        #end##
      #else##
        #if(${attribute.dataType} == 'String')
          #set($line = $line + ',' + "'Sample-" + ${attribute.name} + ${i} + "'")
        #elseif(${attribute.dataType} == 'Date')
          #set($line = $line + ',' + "'2018-08-21'")
        #elseif(${attribute.dataType} == 'Integer')
          #set($line = $line + ',' + "'1000'")
        #end##
      #end##
      #set($index = $index + 1)
    #end##
    (${line.trim()})
  #end##
;
