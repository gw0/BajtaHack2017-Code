<?php
/* @var $this PomenController */
/* @var $model Pomen */
/* @var $form CActiveForm */
?>

<div class="wide form">

<?php $form=$this->beginWidget('CActiveForm', array(
	'action'=>Yii::app()->createUrl($this->route),
	'method'=>'get',
)); ?>

	<div class="row">
		<?php echo $form->label($model,'ID'); ?>
		<?php echo $form->textField($model,'ID'); ?>
	</div>

	<div class="row">
		<?php echo $form->label($model,'SENZOR_ID'); ?>
		<?php echo $form->textField($model,'SENZOR_ID'); ?>
	</div>

	<div class="row">
		<?php echo $form->label($model,'POMEN'); ?>
		<?php echo $form->textField($model,'POMEN',array('size'=>60,'maxlength'=>200)); ?>
	</div>

	<div class="row">
		<?php echo $form->label($model,'MIN_VREDNOST'); ?>
		<?php echo $form->textField($model,'MIN_VREDNOST',array('size'=>60,'maxlength'=>100)); ?>
	</div>

	<div class="row">
		<?php echo $form->label($model,'MAX_VREDNOST'); ?>
		<?php echo $form->textField($model,'MAX_VREDNOST',array('size'=>60,'maxlength'=>100)); ?>
	</div>

	<div class="row buttons">
		<?php echo CHtml::submitButton('Search'); ?>
	</div>

<?php $this->endWidget(); ?>

</div><!-- search-form -->