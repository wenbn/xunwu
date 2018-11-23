package ${package.ManagerImpl};

import ${package.Entity}.${entity};
import ${package.Mapper}.${table.mapperName};
import ${package.Manager}.${table.managerName};
import ${superServiceImplClassPackage};
import org.springframework.stereotype.Service;

/**
 * <p>
 * ${table.comment!} 服务实现类
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */
@Service
<#if kotlin>
open class ${table.managerImplName} ${table.managerName} {

}
<#else>
public class ${table.managerImplName} implements ${table.managerName} {
}
</#if>
