<?php
/* @var $this TipController */
/* @var $model Tip */
/* @var $form CActiveForm */
?>

<div class="form">

<?php $form=$this->beginWidget('CActiveForm', array(
	'id'=>'tip-form',
	// Please note: When you enable ajax validation, make sure the corresponding
	// controller action is handling ajax validation correctly.
	// There is a call to performAjaxValidation() commented in generated controller code.
	// See class documentation of CActiveForm for details on this.
	'enableAjaxValidation'=>false,
)); ?>

	

	<?php echo $form->errorSummary($model); ?>

	<div class="row">
		<?php echo $form->labelEx($model,'NAZIV'); ?>
		<?php echo $form->textField($model,'NAZIV',array('size'=>60,'maxlength'=>200)); ?>
		<?php echo $form->error($model,'NAZIV'); ?>
	</div>

	<div class="row">
		<?php echo $form->labelEx($model,'EM'); ?>
		<?php echo $form->textField($model,'EM',array('size'=>10,'maxlength'=>10)); ?>
		<?php echo $form->error($model,'EM'); ?>
	</div>

	<div class="row">
		<?php echo $form->labelEx($model,'PROTOKOL'); ?>
		<?php echo $form->textField($model,'PROTOKOL'); ?>
		<?php echo $form->error($model,'PROTOKOL'); ?>
	</div>

	<div class="row">
		<?php echo $form->labelEx($model,'DATA'); ?>
		<?php echo $form->textField($model,'DATA',array('size'=>20,'maxlength'=>20)); ?>
		<?php echo $form->error($model,'DATA'); ?>
	</div>

	<div class="row buttons">
		<?php echo CHtml::submitButton($model->isNewRecord ? 'Kreiraj' : 'Save'); ?>
	</div>

<?php $this->endWidget(); ?>

</div><!-- form -->