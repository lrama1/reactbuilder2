package ${packageName}.dao;

#if(${persistenceType} == 'Mongo')
import org.springframework.data.mongodb.repository.MongoRepository;
#else
import org.springframework.data.jpa.repository.JpaRepository;
#end
import org.springframework.stereotype.Repository;

import ${packageName}.domain.$domainClassName;

@Repository
#if(${persistenceType} == 'Mongo')
public interface ${domainClassName}Repository extends MongoRepository<$domainClassName, String> {
#else
    public interface ${domainClassName}Repository extends JpaRepository<$domainClassName, String> {
#end
}
