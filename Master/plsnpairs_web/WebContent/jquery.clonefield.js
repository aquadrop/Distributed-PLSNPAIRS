/*
 * Clonefield 1.1 - jQuery plugin to allow users to duplicate/remove DOM elements
 *
 * Copyright (c) 2008/2009 João Gradim
 *
 * Dual licensed under the MIT and GPL licenses:
 *   http://www.opensource.org/licenses/mit-license.php
 *   http://www.gnu.org/licenses/gpl.html
 *
 */
(function($){

	var _cloned_elems = [];
	var nClonedElems = -1;
	var _cfCounter = 0;
	var _cfClass = "remove-cloned-field";
	$.fn.cloneField = function(elems, options) {
		
		var opts = $.extend({}, $.fn.cloneField.defaults, options);
		
		return this.each(function() {
			var $this = $(this);
			var o = $.meta ? $.extend({}, opts, $this.data()) : opts;
			
			if(!o.useCounter) {
				var hc = '<input type="hidden" id="clonefield-counter" name="clonefield-counter" value="'+o.startVal+'" />';
				$this.after(hc);
				var counterField = $("input#clonefield-counter");
			}
			else {
				var counterField = $("input#"+o.useCounter);
			}
			if(o.allowRemove) {
				_remove_btn = $this.clone(o.clone).attr("id","clonefield-remove").text(o.removeLabel);
				$this.after(_remove_btn);
			}
			_cfCounter = o.startVal;
			$this.bind(o.event, function() {
				if(_cfCounter == o.maxClones + 1 && o.maxClones > 0){
					return false;
				}
				counterField.val(++_cfCounter);
				elems.each(function() {
					
					// new element id and name
					var eID = $(this).attr("id").replace(/\d+$/,"");
					
					if(o.method == "number")
						var eName = $(this).attr("name").replace(/\d+$/,"");
					else
						var eName = $(this).attr("name").replace(/\[\]$/,"");
					
					// label for element
					if(o.label) {
						var eLabel = $("label[for="+$(this).attr("id")+"]").text().replace(/\d+/, _cfCounter);
						console.log(eLabel);
						
						_cloned_elems.push(
							$("label[for="+$(this).attr("id")+"]")
							.clone(o.clone)
							.text(eLabel)
							.attr("for", eID+_cfCounter)
							.insertBefore($this)
							.wrap($(o.labelWrap).addClass(_cfClass+_cfCounter))
							.after($(o.labelAfter).addClass(_cfClass+_cfCounter)))
					}
					
					var _nameSuffix = (o.method == "number") ? _cfCounter : '[]';
					
					// element
					_cloned_elems.push($(this)
						.clone(o.clone)
						.attr("id", eID+_cfCounter)
						.attr("name", eName+_nameSuffix)
						.attr("value", o.value)
						.insertBefore($this)
						.wrap($(o.wrap).addClass(_cfClass+_cfCounter))
						.after($(o.after).addClass(_cfClass+_cfCounter)))
					});
					nClonedElems = (nClonedElems == -1) ? _cloned_elems.length : nClonedElems;
					return false;
			});
			_remove_btn.bind(o.event,function(){
				if(_cfCounter != 1) {
					for(var i = 0; i < nClonedElems; i++) {
						_cloned_elems.pop().remove();
					}
					$("."+_cfClass+_cfCounter).remove();
					counterField.val(--_cfCounter);
				}
				return false
			});
		});
	};
	$.fn.cloneField.defaults = {
		allowRemove: true,		// allow removal of cloned elements; automatically creates removal button based on "add" button markup to create consistency
		removeLabel: "Remove",	// text for removal button
		maxClones: 0,			// maximum number of cloned fields
		label: true,			// create label for cloned elements, based on existing labels
		labelAfter: '',			// html to add after the cloned labels
		labelWrap: '',			// html to wrap cloned labels in
		clone: true,			// copy associated event handlers
		after: '<br />',		// html to add after the cloned elements
		wrap: '',				// html to wrap clone elements in
		value: '',				// default 'value' attribute for cloned elements
		event: 'click',			// event to trigger cloning of elements
		startVal: 1,			// start value of cloned elements
		method:	'array',		// either 'array' (for name="name[]") or 'number' (for name="name1", name="name2", etc...)
		useCounter: false		// use existing hidden field for keeping track of number of cloned elements
	}
})(jQuery);
