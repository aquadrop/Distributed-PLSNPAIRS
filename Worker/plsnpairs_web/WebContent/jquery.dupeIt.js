// JavaScript Document

/*

jQuery Plugin:		jquery.dupeIt
Created by: 		Joel Funk (thecolorme.com)
Last updated:		Apr. 27, 2009

Usage:				$("#selector").dupeIt();

					Options:	$("#selector").dupeIt({
									'buttonclass':string,			// add custom class for buttons, 
																		default class 'dupeit_button'
									'addvalue':string,				// 'add more' button text value
									'removevalue':string,			// 'remove' button text value
									'maxrows':number,				// limits the copies to the number defined,
																		default is 5
								});
						
					Example:
					
					<p>You can create 5 copies.</p>
					<div id="mygroup">
						Name: <input type="text" name="txt_name"> 
						Type: <select name="sel_type">
							<option>1</option>
							<option>2</option>
						</select>
						Image: <input type="file" name="fil_image">
					</div>
					
					<script type="text/javascript">
						$("#mygroup").dupeIt({
							maxrows:5,
							buttonclass:'specialbuttons'
						});
					</script>
					
					Version notes:
					* selector must have an id attribute

*/

jQuery.fn.dupeIt = function(options){
	
	//options 
	var options = jQuery.extend({
		target: null,
		maxrows: 5,
		addvalue: 'Add More',
		removevalue: 'Remove',
		buttonclass: 'dupeit_button',
		iteration: 1,
		addcallback: function(){},
		removecallback: function(k){}
	},options);
	
	//clone info function
	var getcloneinfo = function(target){
		//generate clonelist
		var grabclones = jQuery('"input[id^='+target+']"');
		if(!grabclones) { 
			alert("grabclones error");
			return false
		} else {
			var clonelistlen = jQuery(grabclones).length;
			var clonelist = 'foobar';
			jQuery(grabclones).each(function(){
				clonelist = clonelist + ',' + jQuery(this).attr('id');								 
			});	
	
			//check if hidden field is created for this target
			//the hidden field stores a list of elements cloned from this target
			if(jQuery("#hid_"+target).val()!=null){
				// update value
				//alert("hiddenfield val: "+jQuery("#hid_"+target).val());
				jQuery("#hid_"+target).val(clonelist);
			} else {
				// if not, create hidden field
				var hiddenfield = jQuery(document.createElement('input'))
				.attr({
					'id':'hid_'+target,
					'name':'hid_'+target,
					'value':clonelist,
					'type':'hidden'
				})
				.appendTo(jQuery("#"+target).parent());
				//alert("creating hidden field");
			}
			
			var result = new Object();
			result.items = clonelistlen;
			result.list = clonelist;
			
			return result
		}
	}
	
	var renamechild = function(element,iteration){
		var thischildname = jQuery(element).attr('name'); //get child name
		if(thischildname){
			var newchildname = thischildname + iteration; 
			jQuery(element).attr({'name':newchildname,'value':''});	
		}
	}
	
	//return selected elements
	return this.each(function(){
		
		//check if target is defined
		if(options.target==null) {
			options.target = jQuery(this).attr('id');
			if(!options.target){
				alert("dupeIt requires that all selected elements have a unique id");
				return false
			}
		}
				
		// set iteration 
		var thisgetcloneinfo = getcloneinfo(options.target);
		if(thisgetcloneinfo){
			//var iteration = Number(thisgetcloneinfo.items)+1;
		} else {
			alert("there was an error running getcloneinfo");
			return false	
		}
		
		// set active element
		var thiselement = jQuery(this);
		
		// if parent element is body, wrap selected element in div
		if(jQuery(thiselement).parent().get(0).tagName=="BODY"){
			jQuery(thiselement).wrap(document.createElement("div"));	
		}
		
		// create clone 
		var cloneid = options.target+options.iteration;
		var thisclone = jQuery(thiselement).clone()
		.attr('id',cloneid);
		var thisclonechildren = jQuery(thisclone).children();
		
		// rename cloned children
		jQuery(thisclonechildren).each(function(){
			var thischild = jQuery(this); //get child
			var thisgrandchildren = jQuery(thischild).children();
			renamechild(thischild,options.iteration);
			//grandchidren
			jQuery(thisgrandchildren).each(function(){
				var thisgreatgranchildren = jQuery(this).children();
				renamechild(jQuery(this),options.iteration);
				//great grandchildren
				jQuery(thisgreatgranchildren).each(function(){
					renamechild(jQuery(this),options.iteration);										
				});
			});
			
		});
		
		//add button
		var addbutton = jQuery(document.createElement('input'))
		.attr({
			  'name':'but_dupeit_add_'+options.target,
			  'id':'but_dupeit_add_'+options.target,
			  'class':options.buttonclass,
			  'type':'button',
			  'value':options.addvalue
		})
		.click(function(){
			jQuery(thisclone).insertBefore(jQuery(this)); //add clone to document
			jQuery("#"+options.target).dupeIt({
				target:options.target,
				maxrows:options.maxrows,
				addvalue:options.addvalue,
				removevalue:options.removevalue,
				buttonclass:options.buttonclass,
				iteration:Number(options.iteration+1),
				addcallback:options.addcallback,
				removecallback:options.removecallback
			}); //reset dupeIt
			
			//remove button
			var removebutton = jQuery(document.createElement('input'))
			.attr({
				  'name':'but_dupeit_remove'+options.iteration,
				  'value':options.removevalue,
				  'class':options.buttonclass,
				  'type':'button'
			})
			.click(function(){
				//alert("removing clone, id:"+jQuery(this).parent().attr('id'));
				jQuery(this).parent().remove();
				thisgetcloneinfo = getcloneinfo(options.target); //reset hidden fieldvalue
				// maxrows logic
				if(jQuery('#but_dupeit_add_'+options.target).attr('disabled') != null) {
					jQuery('#but_dupeit_add_'+options.target).removeAttr('disabled');
					jQuery('#but_dupeit_add_'+options.target).show();
				}
				options.removecallback(options.iteration);
			})
			.appendTo(jQuery("#"+cloneid));
			
			//alert("creating clone, id:"+cloneid);
			jQuery(this).remove(); //remove add button
			
			options.addcallback();
		})
		.appendTo(jQuery("#"+options.target).parent());
		
		// maxrows logic
		if(thisgetcloneinfo.items>=options.maxrows) {
			addbutton.attr('disabled','disabled');
			addbutton.hide();
		}

	});
	
}