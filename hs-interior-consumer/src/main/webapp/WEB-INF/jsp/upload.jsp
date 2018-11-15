<%--
  Created by IntelliJ IDEA.
  User: QD
  Date: 2017/12/14
  Time: 10:45
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<script type="text/javascript" src="<%=request.getContextPath() %>/js/jquery-1.7.2.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/jquery.form.js"></script>
<html>
<head>
    <title>Title</title>
</head>
<body>
    <form id="jvForm" method="post">
        <input type="text" name="fileUrl" id="fileUrl" readonly="readonly" />
        <!-- 按钮的onclick事件关联file的onclick事件，点击按钮效果等同于点击file控件 -->
        <input type="button" id="fileButton" name="fileButton" value="浏览" onclick="$('#submitFile').click();" />
        <!-- 隐藏的值file控件改变时同步更新到text上 -->
        <input name="submitFile" id="submitFile" type="file" style="display: none;" onchange="upload_file()" />
        <input type="button" id="delete_file" value="删除" onclick="delete_file1('fileUrl')" />
        <input type="button"  value="下载" onclick="download_file()"/>
        <img src="" id="allUrl" alt="" />

    </form>
</body>
</html>

<script type="text/javascript">
    //上传文件
    function upload_file(){
        var options = {
            url : "<%=request.getContextPath() %>/file/upload",
            dataType : "json",
            type : "post",
            success : function(data){
                var state = data.state;
                if(state==0){
                    alert("文件上传失败");
                }else{
                    $("#allUrl").attr("src",data.url);
                    $("#filedownload").attr("href",data.url);
                    $("#fileUrl").val(data.fid);
                }
            }
        }
        $("#jvForm").ajaxSubmit(options);
    }

    //下载文件
    function download_file(){
        $.ajax({
            type: "POST",
            url: "<%=request.getContextPath() %>/file/download",
            data:{"fileUrl":$("#fileUrl").val()},
            error: function(request) {
                alert('保存信息失败');
            },
            success: function(data) {
                window.open(data.download_url,'_blank');
            }
        });
    }

    //删除文件
    function delete_file1(url){
        var fileId = $('#'+url).val();
       //var fileId = "group1/M00/00/00/wKgCxlozSAyASuXFAAAA1mfdq-4649.txt";
        $.ajax({
            type: "POST",
            url: "<%=request.getContextPath() %>/file/delete",
            data:{"fileId":fileId},
            error: function(data) {
                alert(data.message);
            },
            success: function(data) {
                alert(data.message);
                $("#allUrl").attr("src","");
            }
        });
    }

</script>

