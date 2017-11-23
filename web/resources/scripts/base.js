openHideBlock = function( block ){
    
    try{
        blockObj = document.getElementById(block);
        if( blockObj.style.display == "none" ){
            blockObj.style.display = "block" ;
        }else{
            blockObj.style.display = "none" ;
        }
    }catch(e){}
}

