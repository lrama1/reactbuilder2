package ${packageName}.service;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

//import the domain
import ${packageName}.domain.$domainClassName;
import ${packageName}.common.ListWrapper;
import ${packageName}.dao.${domainClassName}Repository;
import ${packageName}.common.SortedIndicator;

@Service
public class ${domainClassName}Service {
	#set( $repoObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)} + "Repository" )
	#set( $domainObjectName = $domainClassName.substring(0,1).toLowerCase() + $domainClassName.substring(1) )

    @Autowired
            ${domainClassName}Repository $repoObjectName;


	public ListWrapper<${domainClassName}> get${domainClassName}s(int pageNumber, int pageSize, String sortByAttribute, String sortDirection){
        //return ${domainClassName.substring(0,1).toLowerCase()}${domainClassName.substring(1)}DAO.get${domainClassName}s(pageNumber, pageSize, sortByAttribute, sortDirection);

        PageRequest request = PageRequest.of(pageNumber - 1, pageSize);
        if(!"".equals(sortByAttribute)) {
            Sort sortSetting = Sort.by("1".equals(sortDirection)?Direction.ASC: Direction.DESC, sortByAttribute);
            request = PageRequest.of(pageNumber - 1, pageSize, sortSetting);
        }
        Page<${domainClassName}> ${domainObjectName}Page =  ${domainObjectName}Repository.findAll(request);
        ListWrapper<${domainClassName}> results = new ListWrapper<>();
        results.setRows(${domainObjectName}Page.getContent());
        results.setTotalRecords(Long.valueOf(${domainObjectName}Page.getTotalElements()).intValue());
        results.setCurrentPage(pageNumber - 1);
        results.setSortedIndicator(new SortedIndicator(sortByAttribute, sortDirection));
        return results;

    }

	public ${domainClassName} get${domainClassName}(String id){
        return ${domainObjectName}Repository.findById(id).get();
    }


    public void saveNew$domainClassName($domainClassName $domainObjectName){
        ${domainObjectName}.set${domainClassIdAttributeName.substring(0,1).toUpperCase()}${domainClassIdAttributeName.substring(1)}(UUID.randomUUID().toString());
        ${domainObjectName}Repository.saveAndFlush(${domainObjectName});
    }

    public void save$domainClassName($domainClassName $domainObjectName){
        ${domainObjectName}Repository.saveAndFlush(${domainObjectName});
    }
}
