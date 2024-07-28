package  ${packageName}.controller;

import org.springframework.beans.factory.annotation.Autowired;
import java.security.Principal;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;

//import the domain
import ${packageName}.domain.$domainClassName;
import ${packageName}.service.${domainClassName}Service;
import ${packageName}.common.ListWrapper;
import ${packageName}.common.NameValuePair;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.context.MessageSource;

import java.util.List;
import java.util.ArrayList;

@RestController
public class ${domainClassName}Controller {

	#set($serviceObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)} + "Service")
	#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})

    @Autowired
    private ${domainClassName}Service $serviceObjectName;

    @Resource(name = "messageSource")
    private MessageSource messageSource;

    //@PreAuthorize("@sampleUserDetailsService.isAuthorizedToAccessData(#id)")
    @RequestMapping(value = "/$domainObjectName/{id}", method = RequestMethod.GET)
    public $domainClassName get$domainClassName(@PathVariable("id") String id, Principal principal){
        Authentication authenticationToken = (Authentication)principal;
        ${domainClassName} ${domainObjectName} =
        ${domainObjectName}Service.get${domainClassName}(id);
        if(${domainObjectName} == null)
        return new ${domainClassName}();
		else
        return ${domainObjectName};
    }


	@RequestMapping(value = "/${domainObjectName}", headers = {"accept=application/json"}, method = RequestMethod.POST)
	public ${domainClassName} saveNew${domainClassName}(@Valid @RequestBody ${domainClassName} ${domainObjectName}){
        ${serviceObjectName}.saveNew${domainClassName}(${domainObjectName});
        return ${domainClassName.substring(0,1).toLowerCase()}${domainClassName.substring(1)};
    }

	@RequestMapping(value = "/${domainObjectName}/{id}", headers = {"accept=application/json"}, method = RequestMethod.PUT)
	public ${domainClassName}  update${domainClassName}(@Valid @RequestBody ${domainClassName} ${domainObjectName}){
        ${serviceObjectName}.save${domainClassName}(${domainObjectName});
        return ${domainObjectName};
    }

	@RequestMapping("/${domainObjectName}s")
	public ListWrapper<${domainClassName}> getAll${domainClassName}s(
    @RequestParam(value = "page", defaultValue = "1") int pageNumber,
    @RequestParam(value = "per_page", defaultValue="10") int pageSize,
    @RequestParam(value = "sort_by", required = false) String sortByAttributeName,
    @RequestParam(value = "order", required = false) String sortDirection) {
        return ${domainObjectName}Service.get${domainClassName}s(pageNumber, pageSize, sortByAttributeName, sortDirection);

    }

    //=============
	#foreach($key in $attrs.keySet() )
		#if ($fieldTypes.get($key) == "DropDown")

        @RequestMapping("/${key.toLowerCase()}s")
    public @ResponseBody List<NameValuePair> get$key.substring(0,1).toUpperCase()$key.substring(1)s(){
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        NameValuePair nvPair1 = new NameValuePair("sample1", "Sample 1");
        pairs.add(nvPair1);

        NameValuePair nvPair2 = new NameValuePair("sample2", "Sample 2");
        pairs.add(nvPair2);

        return pairs;
    }

		#end
	#end
}
