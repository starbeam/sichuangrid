--删除户籍人口相关权限
delete from rolehaspermissions rp where rp.permissionid in(select p.id from permissions p
　　start with id=(select id from permissions where ename='householdStaffManagement')
　　connect by prior p.id =  p.parentid);
delete from moduelclickcounts m where m.permissionid in(select p.id from permissions p
　　start with id=(select id from permissions where ename='householdStaffManagement')
　　connect by prior p.id =  p.parentid);
delete from permissions p where p.id in(select p.id from permissions p
　　start with id=(select id from permissions where ename='householdStaffManagement')
　　connect by prior p.id =  p.parentid);
--删除流动人口相关权限
delete from rolehaspermissions rp where rp.permissionid in(select p.id from permissions p
　　start with id=(select id from permissions where ename='floatingPopulationManagement')
　　connect by prior p.id =  p.parentid);
delete from moduelclickcounts m 
where m.permissionid in(select p.id from permissions p
　　start with id=(select id from permissions where ename='floatingPopulationManagement')
　　connect by prior p.id =  p.parentid);
delete from permissions p where p.id in(select p.id from permissions p
　　start with id=(select id from permissions where ename='floatingPopulationManagement')
　　connect by prior p.id =  p.parentid);
--删除房屋相关权限
delete from rolehaspermissions rp where rp.permissionid in(select p.id from permissions p
　　start with id=(select id from permissions where ename='actualHouseManagement')
　　connect by prior p.id =  p.parentid);
delete from moduelclickcounts m 
  where m.permissionid in(select p.id from permissions p
　　start with id=(select id from permissions where ename='actualHouseManagement')
　　connect by prior p.id =  p.parentid);
delete from permissions p where p.id in(select p.id from permissions p
　　start with id=(select id from permissions where ename='actualHouseManagement')
　　connect by prior p.id =  p.parentid);
--楼宇里面的住房管理
delete from rolehaspermissions rp where rp.permissionid=(select id from permissions where ename='houseManage');
delete from moduelclickcounts m  where m.permissionid=(select id from permissions where ename='houseManage');
delete from permissions p where p.ename='houseManage';
--未落户里面的落户权限删除
delete from rolehaspermissions rp where rp.permissionid=(select id from permissions where ename='settlePopulation');
delete from moduelclickcounts m  where m.permissionid=(select id from permissions where ename='settlePopulation');
delete from permissions p where p.ename='settlePopulation';
--户籍家庭权限删除
delete from rolehaspermissions rp where rp.permissionid in(select p.id from permissions p
　　start with id=(select id from permissions where ename='houseFamilyInfo')
　　connect by prior p.id =  p.parentid);
delete from moduelclickcounts m 
  where m.permissionid in(select p.id from permissions p
　　start with id=(select id from permissions where ename='houseFamilyInfo')
　　connect by prior p.id =  p.parentid);
delete from permissions p where p.id in(select p.id from permissions p
　　start with id=(select id from permissions where ename='houseFamilyInfo')
　　connect by prior p.id =  p.parentid);
--删除统计报表里面的人口信息下的户籍人口的权限
delete from rolehaspermissions rp where rp.permissionid in(select p.id from permissions p
　　start with id=(select id from permissions where ename='householdStaffStatistic')
　　connect by prior p.id =  p.parentid);
delete from moduelclickcounts m where m.permissionid in(select p.id from permissions p
　　start with id=(select id from permissions where ename='householdStaffStatistic')
　　connect by prior p.id =  p.parentid);
delete from permissions p where p.id in(select p.id from permissions p
　　start with id=(select id from permissions where ename='householdStaffStatistic')
　　connect by prior p.id =  p.parentid);
--删除统计报表里面的人口信息下的流动人口的权限
delete from rolehaspermissions rp where rp.permissionid in(select p.id from permissions p
　　start with id=(select id from permissions where ename='floatingPopulationStatistic')
　　connect by prior p.id =  p.parentid);
delete from moduelclickcounts m where m.permissionid in(select p.id from permissions p
　　start with id=(select id from permissions where ename='floatingPopulationStatistic')
　　connect by prior p.id =  p.parentid);
delete from permissions p where p.id in(select p.id from permissions p
　　start with id=(select id from permissions where ename='floatingPopulationStatistic')
　　connect by prior p.id =  p.parentid);
--删除统计报表里面的房屋管理下的房屋信息的权限
delete from rolehaspermissions rp where rp.permissionid in(select p.id from permissions p
　　start with id=(select id from permissions where ename='actualHouseStatistic')
　　connect by prior p.id =  p.parentid);
delete from moduelclickcounts m where m.permissionid in(select p.id from permissions p
　　start with id=(select id from permissions where ename='actualHouseStatistic')
　　connect by prior p.id =  p.parentid);
delete from permissions p where p.id in(select p.id from permissions p
　　start with id=(select id from permissions where ename='actualHouseStatistic')
　　connect by prior p.id =  p.parentid);
--删除数据管理里面的人口信息下的户籍人口的权限
delete from rolehaspermissions rp where rp.permissionid in(select p.id from permissions p
　　start with id=(select id from permissions where ename='householdStaffTemp')
　　connect by prior p.id =  p.parentid);
delete from moduelclickcounts m where m.permissionid in(select p.id from permissions p
　　start with id=(select id from permissions where ename='householdStaffTemp')
　　connect by prior p.id =  p.parentid);
delete from permissions p where p.id in(select p.id from permissions p
　　start with id=(select id from permissions where ename='householdStaffTemp')
　　connect by prior p.id =  p.parentid);
--删除数据管理里面的人口信息下的流动人口的权限
delete from rolehaspermissions rp where rp.permissionid in(select p.id from permissions p
　　start with id=(select id from permissions where ename='floatingPopulationTemp')
　　connect by prior p.id =  p.parentid);
delete from moduelclickcounts m where m.permissionid in(select p.id from permissions p
　　start with id=(select id from permissions where ename='floatingPopulationTemp')
　　connect by prior p.id =  p.parentid);
delete from permissions p where p.id in(select p.id from permissions p
　　start with id=(select id from permissions where ename='floatingPopulationTemp')
　　connect by prior p.id =  p.parentid);
--删除数据管理里面的房屋信息下的房屋信息的权限
delete from rolehaspermissions rp where rp.permissionid in(select p.id from permissions p
　　start with id=(select id from permissions where ename='actualHouseTemp')
　　connect by prior p.id =  p.parentid);
delete from moduelclickcounts m where m.permissionid in(select p.id from permissions p
　　start with id=(select id from permissions where ename='actualHouseTemp')
　　connect by prior p.id =  p.parentid);
delete from permissions p where p.id in(select p.id from permissions p
　　start with id=(select id from permissions where ename='actualHouseTemp')
　　connect by prior p.id =  p.parentid);